package com.medilabo.microService.transmission.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;


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
		logger.info("Tentative de récupération de toutes les transmissions.");
		List<Transmission> transmissionList = transmissionRepository.findAll();
		if (transmissionList.isEmpty()) {
			logger.warn("Attention, la liste est vide : {}", transmissionList);
		}
		logger.info("Transmissions récupérée : {}", transmissionList);
		return transmissionList;
	}
	
	public Transmission getTransmission(String id) {
		logger.info("Tentative de récupérer une transmission par son Id.");
	    if (id == null || id.isBlank()) {
	        logger.error("L'id ne peut pas être null ou vide. Id = {}", id);
	        throw new IllegalArgumentException("L'id ne peut pas être null ou vide. Id = " + id);
	    }
	    Transmission transmission = transmissionRepository.findById(id)
	    		.orElseThrow(() -> {
	    		    logger.warn("Aucune transmission trouvée");
	    		    return new NoSuchElementException("Aucune transmission trouvée");
	    		});
		logger.info("La transmission récupéré avec l'id {} : {}", id, transmission);
		return transmission;
	}
	
	public Transmission addTransmission(Transmission newTransmission) {
	    logger.info("Tentative d'ajout d'une nouvelle transmission.");
	    if (newTransmission == null) {
	        logger.error("La transmission à ajouter ne peut pas être null.");
	        throw new IllegalArgumentException("La transmission à ajouter ne peut pas être null.");
	    }

	    if (newTransmission.getNomMedecin() == null || newTransmission.getNomMedecin().isBlank()
	            || newTransmission.getPrenomMedecin() == null || newTransmission.getPrenomMedecin().isBlank()
	            || newTransmission.getTransmission() == null || newTransmission.getTransmission().isBlank()) {
	        logger.error("Le nom et prénom du médecin ainsi que la transmission écrite sont obligatoires. La transmission : {}", newTransmission);
	        throw new IllegalArgumentException("Le nom et prénom du médecin ainsi que la transmission écrite sont obligatoires.");
	    }

	    Transmission transmission = new Transmission();
	    transmission.setDateTransmission(LocalDateTime.now());
	    transmission.setNomMedecin(newTransmission.getNomMedecin());
	    transmission.setPrenomMedecin(newTransmission.getPrenomMedecin());
	    transmission.setPatientId(newTransmission.getPatientId());
	    transmission.setTransmission(newTransmission.getTransmission());

	    Transmission transmissionAdded = transmissionRepository.save(transmission);

	    logger.info("La transmission ajouté : {}", transmissionAdded);

	    return transmissionAdded;
	}
	
	public void deleteTransmission(String id) {
		logger.info("Tentative de suppression de la transmission par son id.");
		logger.debug("id de la transmission : {}", id);
		if (id == null || id.isBlank()) {
			logger.error("L'id ne peut pas être null ou vide : {}", id);
			throw new IllegalArgumentException("L'id ne peut pas être null ou vide : "+ id);
		}
		
	    if (!transmissionRepository.existsById(id)) {
	        logger.warn("Aucune transmission trouvée avec l'id : {}", id);
	        throw new NoSuchElementException("Aucune transmission trouvée avec l'id : " + id);
	    }
		
		transmissionRepository.deleteById(id);
	}
	
	public Transmission updateTransmission(String id, Transmission updatedTransmission) {
	    logger.info("Tentative de mise à jour de la transmission avec l'id : {}", id);
	    logger.debug("Transmission reçue pour mise à jour : {}", updatedTransmission);
	    
	    if (id == null || id.isBlank()) {
	        logger.error("L'id ne peut pas être null ou vide : {}", id);
	        throw new IllegalArgumentException("L'id ne peut pas être null ou vide : " + id);
	    }

	    if (updatedTransmission == null) {
	        logger.error("La transmission mise à jour ne peut pas être null.");
	        throw new IllegalArgumentException("La transmission mise à jour ne peut pas être null.");
	    }

	    Transmission existing = transmissionRepository.findById(id)
	            .orElseThrow(() -> {
	                logger.error("Transmission non trouvée avec l'id : {}", id);
	                return new NoSuchElementException("Transmission non trouvée avec l'id : " + id);
	            });
	    
	    logger.debug("La transmission existante : {}", existing);

	    updatedTransmission.setDateTransmission(LocalDateTime.now());

	    Transmission saved = transmissionRepository.save(updatedTransmission);

	    logger.info("Transmission mise à jour avec succès : {}", saved.getId());

	    return saved;
	}
	
	public List<Transmission> getAllTransmissionsByPatientId(String patientId) {
	    logger.info("Tentative de récupération des transmissions du patient avec l'id : {}", patientId);

	    if (patientId == null || patientId.isBlank()) {
	        logger.error("L'id du patient ne peut pas être null ou vide : {}", patientId);
	        throw new IllegalArgumentException("L'id du patient ne peut pas être null ou vide : " + patientId);
	    }

	    List<Transmission> listTransmissionPatient = transmissionRepository.findAllByPatientId(patientId);

	    if (listTransmissionPatient == null || listTransmissionPatient.isEmpty()) {
	        logger.warn("Aucune transmission trouvée pour le patient avec l'id : {}", patientId);
	    } else {
	        logger.info("Transmissions trouvées pour le patient avec l'id {} : {}", patientId, listTransmissionPatient);
	    }
		return listTransmissionPatient;
	}
	
	public String riskEvaluation(List<Transmission> transmissions, String patientId) {
	    logger.info("Début de l'évaluation du risque pour le patient avec l'id : {}", patientId);

	    if (patientId == null || patientId.isBlank()) {
	        logger.error("L'id du patient ne peut pas être null ou vide : {}", patientId);
	        throw new IllegalArgumentException("L'id du patient ne peut pas être null ou vide : " + patientId);
	    }

	    List<Transmission> listTransmission = getAllTransmissionsByPatientId(patientId);

	    if (listTransmission == null || listTransmission.isEmpty()) {
	        logger.warn("Aucune transmission disponible pour le patient avec l'id : {}", patientId);
	        return "none";
	    }

	    List<String> keywords = Arrays.asList(
	        "hémoglobine", "microalbumine", "réaction", "fumeur", "anormal",
	        "vertiges", "gain de poids", "habitué", "crise", "chute", "hors de vue",
	        "déclencheur", "rechute", "aggravé", "éprouver", "peur", "troublé"
	    );

	    long matchingTransmissionsCount = listTransmission.stream()
	        .filter(transmission -> {
	            String content = transmission.getTransmission();
	            if (content == null) {
	                return false;
	            }
	            String contentLower = content.toLowerCase();
	            return keywords.stream().anyMatch(keyword -> contentLower.contains(keyword.toLowerCase()));
	        })
	        .count();

	    logger.info("Nombre de transmissions correspondant aux mots-clés pour le patient {} : {}", patientId, matchingTransmissionsCount);

	    if (matchingTransmissionsCount >= 5) {
	        logger.warn("Risque détecté : DANGER pour le patient {}", patientId);
	        return "danger";
	    } else {
	        logger.info("Aucun risque détecté pour le patient {}", patientId);
	        return "none";
	    }
	}

}
