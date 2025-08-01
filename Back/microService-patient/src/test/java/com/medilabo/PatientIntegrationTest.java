package com.medilabo; // Ajustez le package de votre test

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;
import java.time.Period;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.model.Patient;
import com.medilabo.repository.IPatientRepository;

// Nouveaux imports pour Testcontainers
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers // AJOUTER CETTE ANNOTATION
public class PatientIntegrationTest  {

	@Autowired
	private IPatientRepository patientRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	// Variable pour stocker l'ID du patient de test si nécessaire pour plusieurs tests
	private String patientTestId;

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
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        // Si vous avez besoin d'un nom de base de données spécifique pour vos tests (par défaut 'test'),
        // vous pouvez l'ajouter à l'URI comme ceci, ou spécifier une base de données par défaut dans le conteneur.
        // registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("medilabo_patient_test_db"));
        // Ou plus simplement, si vous n'avez pas de base de données nommée spécifiquement :
        // registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        // registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
        // registry.add("spring.data.mongodb.database", () -> "medilabo_patient_test_db"); // Remplacez par le nom de votre BDD de test si nécessaire
    }

	@BeforeEach // `@BeforeEach` est approprié car il assure un état propre avant chaque test
	void setupPatientData() { // Renommée pour clarté, méthode NON-static
		patientRepository.deleteAll(); // Nettoie la base avant CHAQUE test

		Patient patientTest1 = new Patient();
		patientTest1.setNom("brunet");
		patientTest1.setPrenom("nicolas");
		patientTest1.setTelephone("0612354678");
        patientTest1.setDateNaissance(LocalDate.of(1991, 9, 24)); // Correction date
		patientTest1.setGenre("M"); // Assurez-vous que le genre correspond à votre validation (M/F)
		Patient savedPatient = patientRepository.save(patientTest1);
		this.patientTestId = savedPatient.getId(); // Stocke l'ID pour les tests suivants
		System.out.println("Patient initial 'nbrunet' ajouté avec ID: " + patientTestId);
	}

	@Test
	void getListPatientTest() throws Exception {
		mockMvc.perform(get("/patient/list"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(1)) // Devrait y avoir 1 patient
			.andExpect(jsonPath("$[0].nom").value("brunet"))
			.andExpect(jsonPath("$[0].prenom").value("nicolas"));
	}

    @Test
    void getPatientByIdTest() throws Exception {
        mockMvc.perform(get("/patient/infos/{id}", patientTestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(patientTestId))
            .andExpect(jsonPath("$.nom").value("brunet"))
            .andExpect(jsonPath("$.prenom").value("nicolas"));
    }


    @Test
    void getAgePatientTest() throws Exception {
        // Calcul manuel de l'âge attendu pour vérifier le endpoint
        LocalDate dateNaissance = LocalDate.of(1991, 9, 24);
        int expectedAge = Period.between(dateNaissance, LocalDate.now()).getYears();

        mockMvc.perform(get("/patient/infos/{id}/age", patientTestId))
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(expectedAge))); // Vérifie la valeur de l'âge retournée
    }


    @Test
    void addPatientTest() throws Exception {
        // Supprimer le patient initial pour s'assurer que le test d'ajout commence avec un état connu
        // Ce deleteAll() est en plus de celui du @BeforeEach, il garantit que pour CE test précis
        // on part d'une base complètement vide.
        patientRepository.deleteAll();

        // Nouveau patient à ajouter
        Patient newPatient = new Patient();
        newPatient.setNom("Durand");
        newPatient.setPrenom("Sophie");
        newPatient.setTelephone("0711223344");
        newPatient.setDateNaissance(LocalDate.of(1985, 5, 15));
        newPatient.setGenre("F");

        String newPatientJson = objectMapper.writeValueAsString(newPatient);

        mockMvc.perform(post("/patient/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newPatientJson))
            .andExpect(status().isCreated()) // 201 Created
            .andExpect(jsonPath("$.nom").value("Durand"))
            .andExpect(jsonPath("$.prenom").value("Sophie"));

        // Vérifier qu'il y a maintenant 1 patient dans la base
        mockMvc.perform(get("/patient/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }


    @Test
    void updatePatientTest() throws Exception {
        // Patient modifié (le même patient initial, avec l'ID)
        Patient updatedPatient = new Patient();
        updatedPatient.setId(patientTestId); // Très important: inclure l'ID du patient à modifier
        updatedPatient.setNom("BrunetModifie");
        updatedPatient.setPrenom("NicolasModifie");
        updatedPatient.setTelephone("0699887766"); // Nouvelle valeur
        updatedPatient.setDateNaissance(LocalDate.of(1991, 9, 24));
        updatedPatient.setGenre("M");

        String updatedPatientJson = objectMapper.writeValueAsString(updatedPatient);

        mockMvc.perform(put("/patient/update/{id}", patientTestId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedPatientJson))
            .andExpect(status().isOk()) // 200 OK
            .andExpect(jsonPath("$.nom").value("BrunetModifie"))
            .andExpect(jsonPath("$.telephone").value("0699887766"));

        // Vérifier que la mise à jour a bien été persistée
        mockMvc.perform(get("/patient/infos/{id}", patientTestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value("BrunetModifie"));
    }


    @Test
    void deletePatientTest() throws Exception {
        // S'assurer que le patient est présent avant la suppression
        mockMvc.perform(get("/patient/infos/{id}", patientTestId))
            .andExpect(status().isOk());

        // Effectuer la suppression
        mockMvc.perform(delete("/patient/delete/{id}", patientTestId))
            .andExpect(status().isNoContent()); // 204 No Content

        // Vérifier que le patient a bien été supprimé
        mockMvc.perform(get("/patient/list"))
            .andExpect(status().isOk()) // Ou isNoContent() selon l'implémentation de votre endpoint /list quand vide
            .andExpect(jsonPath("$.length()").value(0));
    }
}