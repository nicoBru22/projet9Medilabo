package com.medilabo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.ArgumentMatchers.any;

import com.medilabo.model.Patient;
import com.medilabo.model.Rdv;
import com.medilabo.repository.IPatientRepository;
import com.medilabo.service.IPatientService;

@SpringBootTest
@ActiveProfiles("test")
public class PatientServiceTest {

	@Autowired
	private IPatientService patientService;
	
	@MockBean
	private IPatientRepository patientRepository;
	private static List<Rdv> rdvList = new ArrayList<>();

	public static Patient p1;
	public static Patient p2;
	public static Patient p3;
	
	@BeforeAll
	public static void setup() {
		p1 = new Patient(1L, "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null, rdvList);
        p2 = new Patient(2L, "Marie", "Durand", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null, rdvList);
        p3 = new Patient(3L, "nico", "Dupuit", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null,  rdvList);

	}
	
	@Test
	public void getAllPatientTest() {
        List<Patient> mockList = Arrays.asList(p1, p2);
        
        when(patientRepository.findAll()).thenReturn(mockList);
        
        List<Patient> result = patientService.getAllPatient();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPrenom()).isEqualTo("Jean");
        assertThat(result.get(1).getPrenom()).isEqualTo("Marie");
        
        verify(patientRepository, times(1)).findAll();
	}
	
	@Test
	public void getPatientByIdTest() {       
        when(patientRepository.findById(1L)).thenReturn(Optional.of(p1));
        
        Patient result = patientService.getPatientById(1L);
        
        assertThat(result).isEqualTo(p1);
        
        verify(patientRepository, times(1)).findById(1L);
	}
	
	@Test
	public void addPatientTest() {
      
        when(patientRepository.save(p1)).thenAnswer(invocation -> invocation.getArgument(0));
        
        Patient result = patientService.addPatient(p1);
        
        assertThat(result.getDateCreation()).isNotNull();
        assertThat(result.getDateModification()).isNotNull();
        assertThat(result).isEqualTo(p1);
        
        verify(patientRepository, times(1)).save(p1);
        
	}
	
	@Test
	public void updatePatientTest() {
        Patient newPatient = new Patient(3L, "sarah", "piet", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null, rdvList);

	    when(patientRepository.findById(3L)).thenReturn(Optional.of(p3));
	    when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    Optional<Patient> result = patientService.updatePatient(newPatient);

	    assertThat(result).isPresent();
	    Patient updated = result.get();
	    assertThat(updated.getPrenom()).isEqualTo("sarah");
	    assertThat(updated.getNom()).isEqualTo("piet");
	    assertThat(updated.getAdresse()).isEqualTo("2 rue B");

	    verify(patientRepository, times(1)).findById(3L);
	    verify(patientRepository, times(1)).save(any(Patient.class));
	}


	@Test
	public void deletePatientTest() {
        when(patientRepository.existsById(p1.getId())).thenReturn(true);
        doNothing().when(patientRepository).deleteById(p1.getId());

        patientService.deletePatient(p1.getId());

        verify(patientRepository, times(1)).existsById(p1.getId());
        verify(patientRepository, times(1)).deleteById(p1.getId());
	}
	
	@Test
	public void calculateAgePatientTest() {
		LocalDate dateNaissanceTest = LocalDate.of(1991, 1, 1);
		
		int result = patientService.agePatient(dateNaissanceTest);
		
		assertThat(result).isEqualTo(34);
		
	}	
	
}
