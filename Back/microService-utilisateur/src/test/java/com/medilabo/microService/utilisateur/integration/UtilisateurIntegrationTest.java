package com.medilabo.microService.utilisateur.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.medilabo.microService.utilisateur.model.User;
import com.medilabo.microService.utilisateur.model.UserDto;
import com.medilabo.microService.utilisateur.repository.IUserRepository;

// Nouveaux imports pour Testcontainers
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers // AJOUTER CETTE ANNOTATION
public class UtilisateurIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token; // Le token JWT obtenu après login

    // DÉCLARATION DU CONTENEUR MONGODB
    // @Container démarre le conteneur et gère son cycle de vie.
    // 'static' assure que le conteneur démarre une seule fois pour toutes les méthodes de test de cette classe.
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
                                                        .withExposedPorts(27017); // Optionnel mais bonne pratique d'exposer le port


    // CONFIGURATION DYNAMIQUE DES PROPRIÉTÉS SPRING
    // Cette méthode est exécutée avant le démarrage du contexte Spring pour injecter
    // l'URI de connexion du conteneur MongoDB démarré.
    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        // Obtenez l'URI de connexion du conteneur MongoDB et configurez-la dans Spring
        // La méthode getReplicaSetUrl() de Testcontainers pour MongoDB est la plus robuste.
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        // Si vous avez besoin d'un nom de base de données spécifique pour vos tests (par défaut 'test'),
        // vous pouvez l'ajouter à l'URI comme ceci, ou spécifier une base de données par défaut dans le conteneur.
        // Exemple : registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("medilabo_test_db"));
        // Ou plus simplement, si vous n'avez pas de base de données nommée spécifiquement :
        // registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        // registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
        // registry.add("spring.data.mongodb.database", () -> "medilabo_test_db"); // Remplacez par le nom de votre BDD de test si nécessaire
    }

    // Modification de @BeforeEach en @BeforeAll pour la méthode setup()
    // Si la génération du token et l'insertion de l'utilisateur admin n'ont besoin d'être faites qu'une seule fois
    // pour toute la suite de tests de cette classe.
    // Si chaque test doit partir d'un état où un utilisateur "nbrunet" est déjà là et un token généré,
    // alors laissez @BeforeEach et ajoutez userRepository.deleteAll(); dans @BeforeEach.
    @BeforeEach // Gardez BeforeEach si vous voulez re-initialiser l'utilisateur et le token pour CHAQUE test.
                // Si vous voulez le faire une seule fois pour tous les tests de la classe, utilisez @BeforeAll
                // et assurez-vous que 'token' est static et initialisé une seule fois.
    public void setup() throws UnsupportedEncodingException, Exception {
        userRepository.deleteAll(); // Vider la collection avant chaque test pour un état propre

        String encodedPwd = passwordEncoder.encode("pwd123");
        User userTest1 = new User(null, "nbrunet", "brunet", "nicolas", encodedPwd, "administrateur");
        userRepository.save(userTest1);

        // 1. Effectuer un login pour obtenir le token
        UserDto credentials = new UserDto("nbrunet", "pwd123");
        String loginJson = objectMapper.writeValueAsString(credentials);

        String response = mockMvc.perform(post("/utilisateur/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        this.token = objectMapper.readTree(response).get("token").asText(); // Affectation à la variable d'instance
    }

    @Test
    public void getListUtilisateurTest() throws Exception{
        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0]").exists())
            .andExpect(jsonPath("$[0].username").value("nbrunet"))
            .andExpect(jsonPath("$[0].prenom").value("nicolas"))
            .andExpect(jsonPath("$[0].nom").value("brunet"))
            .andExpect(jsonPath("$[0].role").value("administrateur"));
    }

    @Test
    public void addUtilisateurTest() throws Exception {
        // Note : userRepository.deleteAll(); est déjà appelé dans @BeforeEach.
        // Si vous voulez partir d'une DB vide SANS l'utilisateur "nbrunet" pour ce test spécifique,
        // vous pouvez l'appeler ici, mais cela peut être redondant ou indiquer que le setup initial n'est pas idéal pour TOUS les tests.
        // Je le supprime ici car il est déjà dans @BeforeEach
        // userRepository.deleteAll();

        String encodedPwd = passwordEncoder.encode("pwd123");
    	User userTest2 = new User(null, "spiet", "piet", "sarah", encodedPwd, "utilisateur");
        String newUserJson = objectMapper.writeValueAsString(userTest2);

        // Le test précédent a déjà vérifié l'utilisateur initial. Ici, on vérifie l'état initial pour add.
        // Vous pouvez laisser ce bloc si vous voulez une vérification explicite avant l'ajout dans ce test.
        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1)) // Devrait être 1, car nbrunet est ajouté dans setup()
            .andExpect(jsonPath("$[0].username").value("nbrunet")); // Vérifie que nbrunet est présent

        mockMvc.perform(post("/utilisateur/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("spiet"));

        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2)) // Maintenant, il devrait y avoir 2 utilisateurs
            .andExpect(jsonPath("$[0].username").value("nbrunet"))
            .andExpect(jsonPath("$[1].username").value("spiet"));
    }

    @Test
    public void deleteByIdTest() throws Exception {
        // Le setup() a déjà inséré "nbrunet".
        // Le test 'deleteByIdTest' s'attend à ce que l'utilisateur 'nbrunet' soit le seul et qu'il soit supprimé.

        // Obtenir l'ID de l'utilisateur "nbrunet" qui a été ajouté dans setup()
        MvcResult result = mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1)) // Confirme qu'il n'y a qu'un seul utilisateur (nbrunet)
            .andExpect(jsonPath("$[0].username").value("nbrunet"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode usersArray = objectMapper.readTree(responseContent);
        String userIdToDelete = usersArray.get(0).get("id").asText();

        mockMvc.perform(delete("/utilisateur/delete/{id}", userIdToDelete)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        // Vérifier que la liste est vide après suppression
        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()) // Ou isNoContent() si votre API renvoie 204 pour une liste vide
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }
}