package com.medilabo.microService.alerte;

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

import com.medilabo.microService.alerte.feign.IPatientClient;
import com.medilabo.microService.alerte.feign.INoteClient;
import com.medilabo.microService.alerte.model.Note;
import com.medilabo.microService.alerte.model.Patient;
import com.medilabo.microService.alerte.service.IAlerteService;

@SpringBootTest
public class MicroServiceAlerteServiceTest {

	@Autowired
	private IAlerteService alerteService;
	
	@MockBean
	private IPatientClient patientClient;
	
	@MockBean
	private INoteClient noteClient;
	
	private static Note noteTest1;
	private static Note noteTestBorderline;
	private static Note noteInDanger;
	private static Note noteEarlyOnset;
	private static Patient patientTest1;
	private static Patient patientTest2;
	
	@BeforeAll
	public static void setup() {
		noteTest1 = new Note(
				"1",
				"1",
				"1",
				LocalDateTime.now(),
				"Brunet",
				"Nicolas",
				"une transmission sans probleme");
		noteTestBorderline = new Note(
				"2",
				"1",
				"2",
				LocalDateTime.now(),
				"Piet",
				"Sarah",
				"une transmission avec 3 problemes : hémoglobine, fumeur, anormal");
		noteInDanger = new Note(
				"3",
				"1",
				"3",
				LocalDateTime.now(),
				"Roux",
				"Marc",
				"Le patient présente les symptômes suivants : HbA1C, Fumeur, Anormal, Réaction, Anticorps, Cholestérol"
			);
		noteEarlyOnset = new Note(
				"4",
				"1",
				"4",
				LocalDateTime.now(),
				"Martin",
				"Julien",
				"Le patient a HbA1C, Microalbumine, Fumeur, Anormal, Réaction, Anticorps, Vertiges, Rechute"
			);
		patientTest1 = new Patient(1L, "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null);
		patientTest2 = new Patient(1L, "Jeanne", "Dupont", LocalDate.of(1990, 1, 1), "feminin", "1 rue A", "0102030405", null, null);

	}
	
	@Test
	public void riskEvaluationNoneTest() {
		Long patientId = 1L;
		int age = 25;
		when(noteClient.getAllNotesPatient(patientId)).thenReturn(List.of(noteTest1));
		when(patientClient.getPatientById(patientId)).thenReturn(patientTest1);
		when(patientClient.getAgePatient(patientId)).thenReturn(age);
		
		String result = alerteService.riskEvaluation(patientId);
		
		assertThat("None").isEqualTo(result);
		
		verify(noteClient, times(1)).getAllNotesPatient(patientId);
		verify(patientClient, times(1)).getPatientById(patientId);
		verify(patientClient, times(1)).getAgePatient(patientId);
	}
	
	@Test
	public void riskEvaluationBorderlineTest() {
		Long patientId = 1L;
		int age = 30;
		when(noteClient.getAllNotesPatient(patientId)).thenReturn(List.of(noteTestBorderline));
		when(patientClient.getPatientById(patientId)).thenReturn(patientTest1);
		when(patientClient.getAgePatient(patientId)).thenReturn(age);
		
		String result = alerteService.riskEvaluation(patientId);
		
		assertThat("Borderline").isEqualTo(result);
		
		verify(noteClient, times(1)).getAllNotesPatient(patientId);
		verify(patientClient, times(1)).getPatientById(patientId);
		verify(patientClient, times(1)).getAgePatient(patientId);
	}
	
	@Test
	public void riskEvaluationInDangerTest() {
		Long patientId = 1L;
		int age = 25;

		when(noteClient.getAllNotesPatient(patientId)).thenReturn(List.of(noteInDanger));
		when(patientClient.getPatientById(patientId)).thenReturn(patientTest2);
		when(patientClient.getAgePatient(patientId)).thenReturn(age);

		String result = alerteService.riskEvaluation(patientId);

		assertThat("In Danger").isEqualTo(result);

		verify(noteClient, times(1)).getAllNotesPatient(patientId);
		verify(patientClient, times(1)).getPatientById(patientId);
		verify(patientClient, times(1)).getAgePatient(patientId);
	}
	
	@Test
	public void riskEvaluationEarlyOnsetTest() {
		Long patientId = 1L;
		int age = 28;

		when(noteClient.getAllNotesPatient(patientId)).thenReturn(List.of(noteEarlyOnset));
		when(patientClient.getPatientById(patientId)).thenReturn(patientTest1);
		when(patientClient.getAgePatient(patientId)).thenReturn(age);

		String result = alerteService.riskEvaluation(patientId);

		assertThat("Early onset").isEqualTo(result);

		verify(noteClient, times(1)).getAllNotesPatient(patientId);
		verify(patientClient, times(1)).getPatientById(patientId);
		verify(patientClient, times(1)).getAgePatient(patientId);
	}


	
	
}
