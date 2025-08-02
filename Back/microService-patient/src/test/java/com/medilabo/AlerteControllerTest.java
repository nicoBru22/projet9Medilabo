package com.medilabo;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.service.IAlerteService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AlerteControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IAlerteService alerteService;
	
	@Test
	public void alerteDiabeteTest() throws Exception {
		String patientId = "123";
		when(alerteService.riskEvaluation(patientId)).thenReturn("Borderline");
		
		mockMvc.perform(get("/patient/diabete")
				.param("patientId", patientId))
				.andExpect(status().isOk())
				.andExpect(content().string("Borderline"));
		
		verify(alerteService, times(1)).riskEvaluation(patientId);
	}
}
