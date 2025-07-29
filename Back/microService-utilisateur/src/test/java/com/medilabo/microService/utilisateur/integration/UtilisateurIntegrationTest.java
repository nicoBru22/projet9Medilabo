package com.medilabo.microService.utilisateur.integration;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilisateurIntegrationTest {


    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    private String token; 

    @BeforeEach
    public void setup() throws UnsupportedEncodingException, Exception {
        userRepository.deleteAll();
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

        this.token = objectMapper.readTree(response).get("token").asText(); // Affectation à la variable d'instance    }
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
        userRepository.deleteAll();
        String encodedPwd = passwordEncoder.encode("pwd123");
    	User userTest2 = new User(null, "spiet", "piet", "sarah", encodedPwd, "utilisateur");
        String newUserJson = objectMapper.writeValueAsString(userTest2);
    	
        userRepository.deleteAll();

        mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    	
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
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].username").value("spiet"))
            .andExpect(jsonPath("$[0].prenom").value("sarah"))
            .andExpect(jsonPath("$[0].nom").value("piet"))
            .andExpect(jsonPath("$[0].role").value("utilisateur"));
    }
    
    @Test
    public void deleteByIdTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/utilisateur/list")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].username").value("nbrunet"))
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
