package com.medilabo.microService.transmission.service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.microService.transmission.model.Transmission;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TransmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITransmissionService transmissionService;

    @Autowired
    private ObjectMapper objectMapper;

    private static Transmission transmissionTest1;
    private static Transmission transmissionTest2;
    private static List<Transmission> listTransmissionTest;

    @BeforeAll
    public static void setup() {
	    ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        transmissionTest1 = new Transmission(
                "1",               // id as String
                "1",               // patientId
                LocalDateTime.now(),
                "docteur",
                "Brunet",
                "Nicolas",
                "une transmission sans probleme"
        );
        transmissionTest2 = new Transmission(
                "2",
                "1",
                LocalDateTime.now(),
                "docteur",
                "Piet",
                "Sarah",
                "une transmission avec 5 problemes : hémoglobine, microalbumine, réaction, fumeur, anormal"
        );
        listTransmissionTest = List.of(transmissionTest1, transmissionTest2);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testAddTransmission() throws Exception {
        when(transmissionService.addTransmission(org.mockito.ArgumentMatchers.any(Transmission.class)))
            .thenReturn(transmissionTest1);
	    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        mockMvc.perform(post("/transmission/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transmissionTest1)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.patientId").value("1"))
            .andExpect(jsonPath("$.transmission").value("une transmission sans probleme"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllTransmissionOfPatient() throws Exception {
        when(transmissionService.getAllTransmissionsByPatientId("1")).thenReturn(listTransmissionTest);

        mockMvc.perform(get("/transmission/getTransmissionsOfPatient")
                .param("patientId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[1].id").value("2"));
    }
}
