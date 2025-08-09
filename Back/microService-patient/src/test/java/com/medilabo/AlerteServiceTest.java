package com.medilabo;

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
import org.springframework.test.context.ActiveProfiles;

import com.medilabo.model.Patient;
import com.medilabo.model.Transmission;
import com.medilabo.service.IAlerteService;
import com.medilabo.service.IPatientService;

@SpringBootTest
@ActiveProfiles("test")
public class AlerteServiceTest {
	
	@Autowired
	private IAlerteService alerteService;
	
	@MockBean
	private IPatientService patientService;
	
	private static Transmission transmissionTest1;
	private static Transmission transmissionTest2;	
	private static Transmission transmissionTest3;	
	private static List<Transmission> listTransmissionTest;
	private static List<Transmission> listTransmissionTest2;
	private static List<Transmission> listTransmissionTest3;
	private static Patient pEarlyOnset;
	private static Patient pNone;
	private static Patient pBorderline;
	private static Patient pInDanger;
	
	@BeforeAll
	public static void setup() {
		transmissionTest1 = new Transmission(
				"1",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Brunet",
				"Nicolas",
				"une transmission avec 4 problemes : Vertiges, Rechute, Réaction, Anticorps");
		transmissionTest2 = new Transmission(
				"2",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Piet",
				"Sarah",
				"une transmission avec 5 problemes : hémoglobine, microalbumine, réaction, fumeur, anormal");
		transmissionTest3 = new Transmission(
				"2",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Piet",
				"Sarah",
				"une transmission sans probleme");
		listTransmissionTest = List.of(transmissionTest1, transmissionTest2);
		listTransmissionTest2 = List.of(transmissionTest3);	
		listTransmissionTest3 = List.of(transmissionTest1);
        pEarlyOnset = new Patient("1", "Jean", "Dupont", LocalDate.of(1980, 1, 1), "masculin", "1 rue A", "0102030405", null, null, listTransmissionTest);
        pNone = new Patient("2", "Jeanne", "Dupuit", LocalDate.of(2000, 1, 1), "feminin", "1 rue A", "0102030405", null, null, listTransmissionTest2);
        pInDanger = new Patient("3", "Jeanne", "Dupuit", LocalDate.of(2000, 1, 1), "feminin", "1 rue A", "0102030405", null, null, listTransmissionTest3);
        pBorderline = new Patient("4", "Jeanne", "Dupuit", LocalDate.of(1980, 1, 1), "feminin", "1 rue A", "0102030405", null, null, listTransmissionTest3);
	}
	
	@Test
	public void patientIsEarlyOnSetTest() {		
		boolean result1 = alerteService.isEarlyOnset(6, 25, "masculin");
		assertThat(true).isEqualTo(result1);
		
		boolean result2 = alerteService.isEarlyOnset(8, 25, "feminin");
		assertThat(true).isEqualTo(result2);
		
		boolean result3 = alerteService.isEarlyOnset(9, 35, "masculin");
		assertThat(true).isEqualTo(result3);
		
		boolean result4 = alerteService.isEarlyOnset(9, 35, "feminin");
		assertThat(true).isEqualTo(result4);
		
		boolean result5 = alerteService.isInDanger(1, 35, "feminin");
		assertThat(false).isEqualTo(result5);
	}
	
	@Test
	public void patientInDangerTest() {
		boolean result1 = alerteService.isInDanger(3, 25, "masculin");
		assertThat(true).isEqualTo(result1);
		
		boolean result2 = alerteService.isInDanger(4, 25, "feminin");
		assertThat(true).isEqualTo(result2);
		
		boolean result3 = alerteService.isInDanger(6, 35, "masculin");
		assertThat(true).isEqualTo(result3);
		
		boolean result4 = alerteService.isInDanger(7, 35, "feminin");
		assertThat(true).isEqualTo(result4);
		
		boolean result5 = alerteService.isInDanger(1, 35, "feminin");
		assertThat(false).isEqualTo(result5);
		
		
	}
	
	@Test
	public void patienBorderlineTest() {
		boolean result1 = alerteService.isBorderline(4, 35);
		assertThat(true).isEqualTo(result1);
		
		boolean result2 = alerteService.isBorderline(7, 25);
		assertThat(false).isEqualTo(result2);
	}
	
	@Test
	public void patientRiskEvaluationEarlyOnsetTest() {
		when(patientService.getPatientById(pEarlyOnset.getId())).thenReturn(pEarlyOnset);
		when(patientService.agePatient(pInDanger.getDateNaissance())).thenReturn(45);

		String result = alerteService.riskEvaluation(pEarlyOnset.getId());
		
		assertThat("Early onset").isEqualTo(result);
		
		verify(patientService, times(1)).getPatientById(pEarlyOnset.getId());
		verify(patientService, times(1)).agePatient(pEarlyOnset.getDateNaissance());
	}
	
	@Test
	public void patientRiskEvaluationInDangerTest() {
		when(patientService.getPatientById(pInDanger.getId())).thenReturn(pInDanger);
		when(patientService.agePatient(pInDanger.getDateNaissance())).thenReturn(25);
		
		String result = alerteService.riskEvaluation(pInDanger.getId());
		
		assertThat("In Danger").isEqualTo(result);
		
		verify(patientService, times(1)).getPatientById(pInDanger.getId());
		verify(patientService, times(1)).agePatient(pInDanger.getDateNaissance());
	}
	
	@Test
	public void patientRiskEvaluationBoderlineTest() {
		when(patientService.getPatientById(pBorderline.getId())).thenReturn(pBorderline);
		when(patientService.agePatient(pBorderline.getDateNaissance())).thenReturn(45);
		String result = alerteService.riskEvaluation(pBorderline.getId());
		
		assertThat("Borderline").isEqualTo(result);
		
		verify(patientService, times(1)).getPatientById(pBorderline.getId());
		verify(patientService, times(1)).agePatient(pBorderline.getDateNaissance());
	}
	
	@Test
	public void patientRiskEvaluationNoneTest() {
		when(patientService.getPatientById(pNone.getId())).thenReturn(pNone);
		when(patientService.agePatient(pNone.getDateNaissance())).thenReturn(25);
		String result = alerteService.riskEvaluation(pNone.getId());
		
		assertThat("None").isEqualTo(result);
		
		verify(patientService, times(1)).getPatientById(pNone.getId());
		verify(patientService, times(1)).agePatient(pNone.getDateNaissance());
	}
	
	@Test
	public void patientNullTest() {
		when(patientService.getPatientById("1")).thenReturn(null);
		
		String result = alerteService.riskEvaluation("1");
		
		assertThat("Aucun risque (Patient non trouvé)").isEqualTo(result);
	}

}
