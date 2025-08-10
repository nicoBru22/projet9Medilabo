package com.medilabo.microService.utilisateur.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
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

import io.github.cdimascio.dotenv.Dotenv;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilisateurIntegrationTest {
	
	private static Logger logger = LogManager.getLogger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    
    @BeforeAll
    static void setup1() {
        boolean isCI = "true".equals(System.getenv("CI"));
        if (!isCI) {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();

            String jwtSecret = dotenv.get("JWT_SECRET");

            if (jwtSecret != null && !jwtSecret.isEmpty()) {
                System.setProperty("JWT_SECRET", jwtSecret);
                logger.info("✅ JWT_SECRET chargé depuis .env dans test integration");
            } else {
                logger.error("❌ JWT_SECRET n'a pas été trouvé dans le fichier .env.");
            }
        } else {
            String jwtSecret = System.getenv("JWT_SECRET");
            if (jwtSecret != null && !jwtSecret.isEmpty()) {
                System.setProperty("JWT_SECRET", jwtSecret);
                logger.info("✅ JWT_SECRET chargé depuis System.getenv dans test integration");
            } else {
                logger.error("❌ JWT_SECRET n'est PAS défini dans les variables d'environnement CI !");
            }
        }
    }

    @BeforeEach
    public void setup() throws UnsupportedEncodingException, Exception {
        userRepository.deleteAll();

        String encodedPwd = passwordEncoder.encode("Password123@");
        User userTest1 = new User(null, "nicolasb", "brunet", "nicolas", encodedPwd, "administrateur");
        userRepository.save(userTest1);

        UserDto credentials = new UserDto("nicolasb", "Password123@");
        String loginJson = objectMapper.writeValueAsString(credentials);

        String response = mockMvc.perform(post("/utilisateur/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        this.token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    public void getListUtilisateurTest() throws Exception{
        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0]").exists())
            .andExpect(jsonPath("$[0].username").value("nicolasb"))
            .andExpect(jsonPath("$[0].prenom").value("nicolas"))
            .andExpect(jsonPath("$[0].nom").value("brunet"))
            .andExpect(jsonPath("$[0].role").value("administrateur"));
    }

    @Test
    public void addUtilisateurTest() throws Exception {
        String encodedPwd = passwordEncoder.encode("pwd123");
    	User userTest2 = new User(null, "sarahPiet", "piet", "sarah", encodedPwd, "utilisateur");
        String newUserJson = objectMapper.writeValueAsString(userTest2);
        
        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].username").value("nicolasb"));

        mockMvc.perform(post("/utilisateur/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("sarahPiet"));

        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").value("nicolasb"))
            .andExpect(jsonPath("$[1].username").value("sarahPiet"));
    }

    @Test
    public void deleteByIdTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].username").value("nicolasb"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode usersArray = objectMapper.readTree(responseContent);
        String userIdToDelete = usersArray.get(0).get("id").asText();

        mockMvc.perform(delete("/utilisateur/delete/{id}", userIdToDelete)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }
}