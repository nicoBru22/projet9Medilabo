package com.medilabo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.ArgumentMatchers.any;

import com.medilabo.model.Patient;
import com.medilabo.model.Transmission;
import com.medilabo.repository.IPatientRepository;
import com.medilabo.service.IPatientService;

@SpringBootTest
@ActiveProfiles("test")
public class PatientServiceTest {

	@Autowired
	private IPatientService patientService;
	
	@MockBean
	private IPatientRepository patientRepository;
	
	private static Transmission transmissionTest1;
	private static Transmission transmissionTest2;
	private static List<Transmission> listTransmissionTest;
	
	@Test
	public void getAllPatientTest() {
		transmissionTest1 = new Transmission(
				"1",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Brunet",
				"Nicolas",
				"une transmission sans probleme");
		transmissionTest2 = new Transmission(
				"2",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Piet",
				"Sarah",
				"une transmission avec 5 problemes : hémoglobine, microalbumine, réaction, fumeur, anormal");
		listTransmissionTest = List.of(transmissionTest1, transmissionTest2);
        Patient p1 = new Patient("1", "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null, listTransmissionTest);
        Patient p2 = new Patient("2", "Marie", "Durand", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null, listTransmissionTest);

        List<Patient> mockList = Arrays.asList(p1, p2);
        
        when(patientRepository.findAll()).thenReturn(mockList);
        
        List<Patient> result = patientService.getAllPatient();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPrenom()).isEqualTo("Jean");
        
        verify(patientRepository, times(1)).findAll();
	}
	
	@Test
	public void getPatientByIdTest() {
        Patient p1 = new Patient("1", "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null, listTransmissionTest);
        
        when(patientRepository.findById("1")).thenReturn(Optional.of(p1));
        
        Patient result = patientService.getPatientById("1");
        
        assertThat(result).isEqualTo(p1);
        
        verify(patientRepository, times(1)).findById("1");
	}
	
	@Test
	public void addPatientTest() {
        Patient p1 = new Patient("1", "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null, listTransmissionTest);
        
        when(patientRepository.save(p1)).thenAnswer(invocation -> invocation.getArgument(0));
        
        Patient result = patientService.addPatient(p1);
        
        assertThat(result.getDateCreation()).isNotNull();
        assertThat(result.getDateModification()).isNotNull();
        assertThat(result).isEqualTo(p1);
        
        verify(patientRepository, times(1)).save(p1);
        
	}
	
	@Test
	public void updatePatientTest() {
	    Patient p1 = new Patient("1", "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null, listTransmissionTest);
	    Patient p2 = new Patient("1", "Marie", "Durand", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null, listTransmissionTest);

	    when(patientRepository.findById("1")).thenReturn(Optional.of(p1));
	    when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    Optional<Patient> result = patientService.updatePatient(p2);

	    assertThat(result).isPresent();
	    Patient updated = result.get();
	    assertThat(updated.getPrenom()).isEqualTo("Marie");
	    assertThat(updated.getNom()).isEqualTo("Durand");
	    assertThat(updated.getAdresse()).isEqualTo("2 rue B");

	    verify(patientRepository, times(1)).findById("1");
	    verify(patientRepository, times(1)).save(any(Patient.class));
	}


	@Test
	public void deletepPatientByIdTest() {
		
		
	}
	
	@Test
	public void calculateAgePatientTest() {
		LocalDate dateNaissanceTest = LocalDate.of(1991, 1, 1);
		
		int result = patientService.agePatient(dateNaissanceTest);
		
		assertThat(result).isEqualTo(34);
		
	}
	
	
}
