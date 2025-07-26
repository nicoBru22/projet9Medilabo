package com.medilabo.microService.anticipation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.medilabo.microService.anticipation.feign.IPatientClient;
import com.medilabo.microService.anticipation.feign.ITransmissionClient;
import com.medilabo.microService.anticipation.model.Patient;
import com.medilabo.microService.anticipation.service.IAlertAnticipationService;
import com.medilabo.microService.anticipation.model.Transmission;

@SpringBootTest
public class MicroServiceAnticipationServiceTest {

	@Autowired
	private IAlertAnticipationService anticipationService;
	
	@MockBean
	private IPatientClient patientClient;
	
	@MockBean
	private ITransmissionClient transmissionClient;
	
	private static Transmission transmissionTest1;
	private static Transmission transmissionTestBorderline;
	private static Transmission transmissionInDanger;
	private static Transmission transmissionEarlyOnset;
	private static Patient patientTest1;
	private static Patient patientTest2;
	
	@BeforeAll
	public static void setup() {
		transmissionTest1 = new Transmission(
				"1",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Brunet",
				"Nicolas",
				"une transmission sans probleme");
		transmissionTestBorderline = new Transmission(
				"2",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Piet",
				"Sarah",
				"une transmission avec 3 problemes : hémoglobine, fumeur, anormal");
		transmissionInDanger = new Transmission(
				"3",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Roux",
				"Marc",
				"Le patient présente les symptômes suivants : HbA1C, Fumeur, Anormal, Réaction, Anticorps, Cholestérol"
			);
		transmissionEarlyOnset = new Transmission(
				"4",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Martin",
				"Julien",
				"Le patient a HbA1C, Microalbumine, Fumeur, Anormal, Réaction, Anticorps, Vertiges, Rechute"
			);
		patientTest1 = new Patient("1", "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null);
		patientTest2 = new Patient("1", "Jeanne", "Dupont", LocalDate.of(1990, 1, 1), "feminin", "1 rue A", "0102030405", null, null);

	}
	
	@Test
	public void riskEvaluationNoneTest() {
		String patientId = "1L";
		int age = 25;
		when(transmissionClient.getAllTransmissionOfPatient(patientId)).thenReturn(List.of(transmissionTest1));
		when(patientClient.getPatientById(patientId)).thenReturn(patientTest1);
		when(patientClient.getAgePatient(patientId)).thenReturn(age);
		
		String result = anticipationService.riskEvaluation(patientId);
		
		assertThat("None").isEqualTo(result);
		
		verify(transmissionClient, times(1)).getAllTransmissionOfPatient(patientId);
		verify(patientClient, times(1)).getPatientById(patientId);
		verify(patientClient, times(1)).getAgePatient(patientId);
	}
	
	@Test
	public void riskEvaluationBorderlineTest() {
		String patientId = "1L";
		int age = 30;
		when(transmissionClient.getAllTransmissionOfPatient(patientId)).thenReturn(List.of(transmissionTestBorderline));
		when(patientClient.getPatientById(patientId)).thenReturn(patientTest1);
		when(patientClient.getAgePatient(patientId)).thenReturn(age);
		
		String result = anticipationService.riskEvaluation(patientId);
		
		assertThat("Borderline").isEqualTo(result);
		
		verify(transmissionClient, times(1)).getAllTransmissionOfPatient(patientId);
		verify(patientClient, times(1)).getPatientById(patientId);
		verify(patientClient, times(1)).getAgePatient(patientId);
	}
	
	@Test
	public void riskEvaluationInDangerTest() {
		String patientId = "1";
		int age = 25;

		when(transmissionClient.getAllTransmissionOfPatient(patientId)).thenReturn(List.of(transmissionInDanger));
		when(patientClient.getPatientById(patientId)).thenReturn(patientTest2);
		when(patientClient.getAgePatient(patientId)).thenReturn(age);

		String result = anticipationService.riskEvaluation(patientId);

		assertThat("In Danger").isEqualTo(result);

		verify(transmissionClient, times(1)).getAllTransmissionOfPatient(patientId);
		verify(patientClient, times(1)).getPatientById(patientId);
		verify(patientClient, times(1)).getAgePatient(patientId);
	}
	
	@Test
	public void riskEvaluationEarlyOnsetTest() {
		String patientId = "1";
		int age = 28;

		when(transmissionClient.getAllTransmissionOfPatient(patientId)).thenReturn(List.of(transmissionEarlyOnset));
		when(patientClient.getPatientById(patientId)).thenReturn(patientTest1);
		when(patientClient.getAgePatient(patientId)).thenReturn(age);

		String result = anticipationService.riskEvaluation(patientId);

		assertThat("Early onset").isEqualTo(result);

		verify(transmissionClient, times(1)).getAllTransmissionOfPatient(patientId);
		verify(patientClient, times(1)).getPatientById(patientId);
		verify(patientClient, times(1)).getAgePatient(patientId);
	}


	
	
}
