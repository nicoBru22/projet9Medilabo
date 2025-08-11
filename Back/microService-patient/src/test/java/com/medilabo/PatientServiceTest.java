package com.medilabo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	private static List<Transmission> listTransmissionEmpty;
	private static List<Rdv> rdvList = new ArrayList<>();

	public static Patient p1;
	public static Patient p2;
	public static Patient p3;
	
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
		transmissionTest2 = new Transmission(
				"2",
				"1",
				LocalDateTime.now(),
				"docteur",
				"Piet",
				"Sarah",
				"une transmission avec 5 problemes : hémoglobine, microalbumine, réaction, fumeur, anormal");
		listTransmissionTest = List.of(transmissionTest1, transmissionTest2);
		listTransmissionEmpty = new ArrayList<>();
		p1 = new Patient("1", "Jean", "Dupont", LocalDate.of(1990, 1, 1), "masculin", "1 rue A", "0102030405", null, null, listTransmissionTest, rdvList);
        p2 = new Patient("2", "Marie", "Durand", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null, listTransmissionTest, rdvList);
        p3 = new Patient("3", "nico", "Dupuit", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null, listTransmissionEmpty, rdvList);

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
        when(patientRepository.findById("1")).thenReturn(Optional.of(p1));
        
        Patient result = patientService.getPatientById("1");
        
        assertThat(result).isEqualTo(p1);
        
        verify(patientRepository, times(1)).findById("1");
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
        Patient newPatient = new Patient("3", "sarah", "piet", LocalDate.of(1985, 5, 10), "feminin", "2 rue B", "0607080910", null, null, listTransmissionEmpty, rdvList);

	    when(patientRepository.findById("3")).thenReturn(Optional.of(p3));
	    when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    Optional<Patient> result = patientService.updatePatient(newPatient);

	    assertThat(result).isPresent();
	    Patient updated = result.get();
	    assertThat(updated.getPrenom()).isEqualTo("sarah");
	    assertThat(updated.getNom()).isEqualTo("piet");
	    assertThat(updated.getAdresse()).isEqualTo("2 rue B");

	    verify(patientRepository, times(1)).findById("3");
	    verify(patientRepository, times(1)).save(any(Patient.class));
	}


	@Test
	public void deletePatientTest() {
        when(patientRepository.existsById(p1.getId())).thenReturn(true);
        // Simule l'appel à deleteById qui ne renvoie rien (void)
        doNothing().when(patientRepository).deleteById(p1.getId());

        patientService.deletePatient(p1.getId());

        // Vérifications :
        // 1. On vérifie que existsById a été appelé une fois
        verify(patientRepository, times(1)).existsById(p1.getId());
        // 2. On vérifie que deleteById a été appelé une fois
        verify(patientRepository, times(1)).deleteById(p1.getId());
	}
	
	@Test
	public void calculateAgePatientTest() {
		LocalDate dateNaissanceTest = LocalDate.of(1991, 1, 1);
		
		int result = patientService.agePatient(dateNaissanceTest);
		
		assertThat(result).isEqualTo(34);
		
	}
	
	@Test
	public void addTransmissionServiceTest() {
	    Transmission newTransmission = new Transmission(
	            null, // L'ID sera généré par le service
	            null, // patientId sera passé en paramètre
	            null, // Date de transmission sera générée par le service
	            "Dr.",
	            "Lambda",
	            "Jean",
	            "Nouvelle observation du patient."
	    );
	    
		when(patientRepository.existsById(p3.getId())).thenReturn(true);
		when(patientRepository.findById(p3.getId())).thenReturn(Optional.of(p3));
	    when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    
	    Transmission result = patientService.addTransmission(newTransmission, p3.getId());
	    
	    assertThat(result).isNotNull();
	    assertThat(result.getId()).isNotNull();
	    assertThat(result.getDateTransmission()).isNotNull();
	    assertThat(result.getPatientId()).isEqualTo(p3.getId());
	    assertThat(result.getTransmission()).isEqualTo("Nouvelle observation du patient.");
	    
	    
	    verify(patientRepository, times(1)).existsById(p3.getId());
	    verify(patientRepository, times(1)).findById(p3.getId());
	    verify(patientRepository, times(1)).save(any(Patient.class));
	}
	
	@Test
	public void getTransmissionsOfPatientTest() {
	
		when(patientRepository.existsById(p1.getId())).thenReturn(true);
		when(patientRepository.findById(p1.getId())).thenReturn(Optional.of(p1));
		
		List<Transmission> result = patientService.getAllTransmissionOfPatient(p1.getId());
		
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getTransmission()).isEqualTo("une transmission sans probleme");
		assertThat(result.get(1).getTransmission()).isEqualTo("une transmission avec 5 problemes : hémoglobine, microalbumine, réaction, fumeur, anormal");
		
	    verify(patientRepository, times(1)).existsById(p1.getId());
	    verify(patientRepository, times(1)).findById(p1.getId());
	}
	
	
}
