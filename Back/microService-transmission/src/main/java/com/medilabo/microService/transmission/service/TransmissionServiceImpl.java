package com.medilabo.microService.transmission.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medilabo.microService.transmission.model.Transmission;
import com.medilabo.microService.transmission.repository.ITransmissionRepository;

@Service
public class TransmissionServiceImpl implements ITransmissionService {
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private ITransmissionRepository transmissionRepository;
	
	public List<Transmission> getAllTransmissions() {
		logger.info("");
		List<Transmission> transmissionList = transmissionRepository.findAll();
		logger.info("");
		return transmissionList;
	}
	
	public Transmission getTransmission(String id) {
		logger.info("");
		Optional<Transmission> transmisson = transmissionRepository.findById(id);
		logger.info("");
		return transmisson.get();
	}
	
	public Transmission addTransmission(Transmission newTransmission) {
	    logger.info("Création d'une nouvelle transmission manuellement");

	    Transmission transmission = new Transmission();
	    transmission.setDateTransmission(LocalDateTime.now());
	    transmission.setNomMedecin(newTransmission.getNomMedecin());
	    transmission.setPrenomMedecin(newTransmission.getPrenomMedecin());
	    transmission.setPatientId(newTransmission.getPatientId());
	    transmission.setTransmission(newTransmission.getTransmission());

	    transmissionRepository.save(transmission);

	    logger.info("Transmission créée avec succès");

	    return transmission;
	}
	
	public void deleteTransmission(String id) {
		transmissionRepository.deleteById(id);
	}
	
	public Transmission updateTransmission(String id, Transmission updatedTransmission) {
	    logger.info("Mise à jour de la transmission avec l'id : {}", id);

	    Transmission existing = transmissionRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Transmission non trouvée avec l'id : " + id));

	    // On conserve l'id et met à jour la date
	    updatedTransmission.setId(id);
	    updatedTransmission.setDateTransmission(LocalDateTime.now());

	    Transmission saved = transmissionRepository.save(updatedTransmission);

	    logger.info("Transmission mise à jour avec succès : {}", saved.getId());

	    return saved;
	}
	
	public List<Transmission> getAllTransmissionsByPatientId(String patientId) {
		logger.info("Tentative de récupération des transmissions du patient avec l'id : {}", patientId);
		List<Transmission> listTransmissionPatient = transmissionRepository.findAllByPatientId(patientId);
		logger.info("Les ids transmissions du patient sont : {}",listTransmissionPatient );
		return listTransmissionPatient;
	}
	
	public String riskEvaluation (List<Transmission> transmissions, String patientId) { // Changement du type de retour à String

	    List<Transmission> listTransmission = getAllTransmissionsByPatientId(patientId);

	    List<String> keywords = Arrays.asList(
	        "hémoglobine", "microalbumine", "réaction", "fumeur", "anormal",
	        "vertiges", "gain de poids", "habitué", "crise", "chute", "hors de vue",
	        "déclencheur", "rechute", "aggravé", "éprouver", "peur", "troublé"
	    );

	    long matchingTransmissionsCount = listTransmission.stream()
	            .filter(transmission ->
	                keywords.stream().anyMatch(keyword ->
	                    transmission.getTransmission().toLowerCase().contains(keyword.toLowerCase())
	                )
	            )
	            .count();
	    
	    if (matchingTransmissionsCount >= 5) { 
	        return "danger";
	    } else {
	        return "none";
	    }
	}
}
