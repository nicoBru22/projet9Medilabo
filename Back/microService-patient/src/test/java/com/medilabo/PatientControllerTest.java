package com.medilabo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.model.Patient;
import com.medilabo.model.Rdv;
import com.medilabo.service.IPatientService;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IPatientService patientService;
	
	private static Patient patientTest1;
	private static Patient patientTest2;
	private static List<Rdv> rdvList = new ArrayList<>();


	@BeforeAll
	public static void setup() {
        patientTest1 = new Patient(
                1L,
                "Nicolas",
                "Brunet",
                LocalDate.of(1990, 5, 20),
                "Homme",
                "10 rue des Lilas",
                "0123456789",
                LocalDateTime.now(),
                LocalDateTime.now(),
                rdvList
            );
        patientTest2 = new Patient(
                2L,
                "Sarah",
                "Piet",
                LocalDate.of(1990, 5, 20),
                "Femme",
                "10 rue des Lilas",
                "0123456789",
                LocalDateTime.now(),
                LocalDateTime.now(),
                rdvList
            );
	}

	@Test
    @WithMockUser(username = "testuser", roles = {"USER"})
	public void getAllPatientTest() throws Exception {
		List<Patient> listPatient = List.of(patientTest1, patientTest2);
		
		when(patientService.getAllPatient()).thenReturn(listPatient);
		
		mockMvc.perform(get("/patient/list"))
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].prenom").value("Nicolas"))
        .andExpect(jsonPath("$[1].prenom").value("Sarah")); 
		
		verify(patientService, times(1)).getAllPatient();
	}
	
	@Test
    @WithMockUser(username = "testuser", roles = {"USER"})
	public void getPatientByIdControllerTest() throws Exception {
		when(patientService.getPatientById(1L)).thenReturn(patientTest1);
		
	    mockMvc.perform(get("/patient/infos/{id}", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.prenom").value("Nicolas"))
        .andExpect(jsonPath("$.nom").value("Brunet"));

	    verify(patientService, times(1)).getPatientById(1L);
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = {"USER"})
	public void addPatientTest() throws Exception {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
	    String patientJson = objectMapper.writeValueAsString(patientTest1);

	    when(patientService.addPatient(any(Patient.class))).thenReturn(patientTest1);

	    mockMvc.perform(post("/patient/add")
	            .contentType("application/json")
	            .content(patientJson))
	            .andExpect(status().isCreated())
	            .andExpect(jsonPath("$.prenom").value("Nicolas"))
	            .andExpect(jsonPath("$.nom").value("Brunet"));

	    verify(patientService, times(1)).addPatient(any(Patient.class));	}

	@Test
	@WithMockUser(username = "testuser", roles = {"USER"})
	public void deletePatientControllerTest() throws Exception {
		doNothing().when(patientService).deletePatient(1L);
	    
	    mockMvc.perform(delete("/patient/delete/{id}", 1L))
	        .andExpect(status().isNoContent());

	    verify(patientService, times(1)).deletePatient(1L);
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = {"USER"})
	public void updatePatientControllerTest() throws Exception {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
	    String patientJson = objectMapper.writeValueAsString(patientTest2);
	    
	    when(patientService.updatePatient(any(Patient.class))).thenReturn(Optional.of(patientTest2));
	    
	    mockMvc.perform(put("/patient/update/{id}", 1L)
	            .contentType("application/json")
	            .content(patientJson))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.prenom").value("Sarah"))
	        .andExpect(jsonPath("$.nom").value("Piet"));

	    verify(patientService, times(1)).updatePatient(any(Patient.class));
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = {"USER"})
	public void getAgeControllerTest() throws Exception {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
	    String patientJson = objectMapper.writeValueAsString(patientTest2);
	    
	    when(patientService.updatePatient(any(Patient.class))).thenReturn(Optional.of(patientTest2));
	    
	    mockMvc.perform(put("/patient/update/{id}", 1L)
	            .contentType("application/json")
	            .content(patientJson))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.prenom").value("Sarah"))
	        .andExpect(jsonPath("$.nom").value("Piet"));

	    verify(patientService, times(1)).updatePatient(any(Patient.class));
	}	

}
