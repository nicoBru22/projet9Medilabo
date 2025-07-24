package com.medilabo.microService.transmission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.medilabo.microService.transmission.model.Transmission;
import com.medilabo.microService.transmission.repository.ITransmissionRepository;

@SpringBootTest
public class TransmissionServiceTest {
	
	@MockBean
	private ITransmissionRepository transmissionRepository;

	@Autowired
	private ITransmissionService transmissionService;
	
	private static Transmission transmissionTest1;
	private static Transmission transmissionTest2;
	private static List<Transmission> listTransmissionTest;
	
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
	}
	
	@Test
	public void getAllTransmissionServiceTest() {
		when(transmissionRepository.findAll()).thenReturn(listTransmissionTest);
		
		List<Transmission> result = transmissionService.getAllTransmissions();

	    assertThat(2).isEqualTo(result.size());
	    
	    assertThat(result).contains(transmissionTest1, transmissionTest2);
		
		verify(transmissionRepository, times(1)).findAll();
	}
	
	@Test
	public void getTransmissionServiceTest() {
		when(transmissionRepository.findById("1")).thenReturn(Optional.of(transmissionTest1));
		
		Transmission result = transmissionService.getTransmission("1");
		
		assertThat(transmissionTest1).isEqualTo(result);
		
		verify(transmissionRepository, times(1)).findById("1");
	}
	
	@Test
	public void addTransmissionServiceTest() {
		when(transmissionRepository.save(any(Transmission.class))).thenReturn(transmissionTest1);
		
        Transmission result = transmissionService.addTransmission(transmissionTest1);
        
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(transmissionTest1);
        
        verify(transmissionRepository, times(1)).save(any(Transmission.class));
        
	}
	
	@Test
	public void deleteTransmissionServiceTest() {
        when(transmissionRepository.existsById("1")).thenReturn(true);
        
        doNothing().when(transmissionRepository).deleteById("1");

        transmissionService.deleteTransmission("1");

        verify(transmissionRepository, times(1)).existsById("1");
        verify(transmissionRepository, times(1)).deleteById("1");
	}
	
	@Test
	public void updateTransmissionServiceTest() {
        when(transmissionRepository.findById("1")).thenReturn(Optional.of(transmissionTest1));
        
        when(transmissionRepository.save(any(Transmission.class))).thenReturn(transmissionTest1);
        
        Transmission result = transmissionService.updateTransmission("1", transmissionTest1);
        
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(transmissionTest1);
        
        verify(transmissionRepository, times(1)).findById("1");
        verify(transmissionRepository, times(1)).save(any(Transmission.class));
	}
	
	@Test
	public void getAllTransmissionsByPatientIdServiceTest() {
		when(transmissionRepository.findAllByPatientId("1")).thenReturn(listTransmissionTest);
		
		List<Transmission> result = transmissionService.getAllTransmissionsByPatientId("1");
		
		assertThat(result).isNotNull();
		assertThat(result).contains(transmissionTest1, transmissionTest2);
		
        verify(transmissionRepository, times(1)).findAllByPatientId("1");
		
	}
	
	@Test
	public void riskEvaluationNoneServiceTest() {
		List<Transmission> listNone = List.of(transmissionTest1);
		
		when(transmissionRepository.findAllByPatientId("1")).thenReturn(listNone);
		
		String result = transmissionService.riskEvaluation(listNone, "1");
		
		assertThat(result).isEqualTo("none");
		
		verify(transmissionRepository, times(1)).findAllByPatientId("1");
	}
	
	@Test
	public void riskEvaluationDangerServiceTest() {
	    List<Transmission> listDanger = List.of(transmissionTest2); // 1 transmission contenant plusieurs mots-clés
	    
	    // Mock du repository pour que la méthode interne getAllTransmissionsByPatientId retourne cette liste
	    when(transmissionRepository.findAllByPatientId("2")).thenReturn(listDanger);

	    String result = transmissionService.riskEvaluation(listDanger, "2");

	    assertThat(result).isEqualTo("none"); // car il faut au moins 5 transmissions contenant les mots-clés
	}


	
	
}
