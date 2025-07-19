package com.medilabo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.model.Patient;
import com.medilabo.repository.IPatientRepository;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private IPatientRepository patientRepository;
	
    private Patient p3;
	
	@BeforeEach
	public void setup() {
		patientRepository.deleteAll();
	    Patient p1 = new Patient("1", "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null);
	    Patient p2 = new Patient("1", "Marie", "Durand", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null);
	    p3 = new Patient("1", "Nicolas", "Brunet", LocalDate.of(1991, 5, 10), "masculin", "564 fautrel", "0611223344", null, null);
	    
	    patientRepository.save(p1);
	    patientRepository.save(p2);
	    
	}
	

	@Test
	public void addPatientTest() throws Exception {
	    ObjectMapper mapper = new ObjectMapper();
	    
	    String patientJson = mapper.writeValueAsString(p3);
	    
	    mockMvc.perform(post("/patient/add")
	            .contentType("application/json")
	            .content(patientJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.prenom").value("Nicolas"))
        .andExpect(jsonPath("$.nom").value("Brunet"));  
	}
	
	@Test
	public void getAllPatientTest() throws Exception {
		mockMvc.perform(get("/patient/list"))
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].prenom").value("Jean"))
        .andExpect(jsonPath("$[1].prenom").value("Marie")); 
	}
	

	@Test
	public void deletePatientTest() throws Exception {
	    mockMvc.perform(get("/patient/list"))
	        .andExpect(content().contentType("application/json"))
	        .andExpect(jsonPath("$.length()").value(2))
	        .andExpect(jsonPath("$[0].prenom").value("Jean"))
	        .andExpect(jsonPath("$[1].prenom").value("Marie"));
	    
	    mockMvc.perform(delete("/patient/delete/{id}", "1"))
	        .andExpect(status().isNoContent());
	    
	    mockMvc.perform(get("/patient/list"))
	        .andExpect(content().contentType("application/json"))
	        .andExpect(jsonPath("$.length()").value(1))
	        .andExpect(jsonPath("$[0].prenom").value("Marie"));
	}

}
