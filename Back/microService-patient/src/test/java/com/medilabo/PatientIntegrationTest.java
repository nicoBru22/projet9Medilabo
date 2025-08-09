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

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class PatientIntegrationTest  {

	@Autowired
	private IPatientRepository patientRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;


	private String patientTestId;


    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
                                                        .withExposedPorts(27017);

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "medilabo_patient_test_db");
    }

	@BeforeEach
	void setupPatientData() {
		patientRepository.deleteAll();

		Patient patientTest1 = new Patient();
		patientTest1.setNom("brunet");
		patientTest1.setPrenom("nicolas");
		patientTest1.setTelephone("0612354678");
        patientTest1.setDateNaissance(LocalDate.of(1991, 9, 24));
		patientTest1.setGenre("M");
		Patient savedPatient = patientRepository.save(patientTest1);
		this.patientTestId = savedPatient.getId();
		System.out.println("Patient initial 'nbrunet' ajouté avec ID: " + patientTestId);
	}

	@Test
	void getListPatientTest() throws Exception {
		mockMvc.perform(get("/patient/list"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(1))
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
        LocalDate dateNaissance = LocalDate.of(1991, 9, 24);
        int expectedAge = Period.between(dateNaissance, LocalDate.now()).getYears();

        mockMvc.perform(get("/patient/infos/{id}/age", patientTestId))
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(expectedAge)));
    }


    @Test
    void addPatientTest() throws Exception {
        patientRepository.deleteAll();

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
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nom").value("Durand"))
            .andExpect(jsonPath("$.prenom").value("Sophie"));

        mockMvc.perform(get("/patient/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }


    @Test
    void updatePatientTest() throws Exception {
        Patient updatedPatient = new Patient();
        updatedPatient.setId(patientTestId);
        updatedPatient.setNom("BrunetModifie");
        updatedPatient.setPrenom("NicolasModifie");
        updatedPatient.setTelephone("0699887766");
        updatedPatient.setDateNaissance(LocalDate.of(1991, 9, 24));
        updatedPatient.setGenre("M");

        String updatedPatientJson = objectMapper.writeValueAsString(updatedPatient);

        mockMvc.perform(put("/patient/update/{id}", patientTestId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedPatientJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value("BrunetModifie"))
            .andExpect(jsonPath("$.telephone").value("0699887766"));

        mockMvc.perform(get("/patient/infos/{id}", patientTestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value("BrunetModifie"));
    }


    @Test
    void deletePatientTest() throws Exception {
        mockMvc.perform(get("/patient/infos/{id}", patientTestId))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/patient/delete/{id}", patientTestId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/patient/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}