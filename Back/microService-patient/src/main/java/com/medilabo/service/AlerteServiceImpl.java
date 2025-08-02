package com.medilabo.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medilabo.model.Patient;
import com.medilabo.model.Transmission;

/**
 * Service permettant d'évaluer le risque de santé d'un patient en fonction de mots-clés
 * retrouvés dans ses transmissions médicales et de ses données personnelles (âge, genre).
 */
@Service
public class AlerteServiceImpl implements IAlerteService {
	
    /**
     * Liste des mots-clés déclencheurs utilisés pour détecter un risque médical.
     */
	private List<String> listMotCle = List.of(
		    "HbA1C", "Microalbumine", "Taille", "Poids", "Fumeur", "Fumer",
		    "Fumeuse", "Anormal", "Cholestérol", "Vertiges", "Rechute", "Réaction", "Anticorps"
		);
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private IPatientService patientService;
	
    /**
     * Évalue le risque médical d’un patient en analysant ses transmissions et données personnelles.
     *
     * @param patientId l'identifiant du patient
     * @return une chaîne décrivant le niveau de risque : "Early onset", "In Danger", "Borderline", ou "None"
     */
	public String riskEvaluation (String patientId) {
		logger.info("Tentative de récupération de l'alerte santé pour le patient : {}", patientId);
		Patient patient = patientService.getPatientById(patientId);
		
	    if (patient == null) {
	    	logger.warn("Patient non trouvé avec l'ID : {}. Impossible d'évaluer le risque.", patientId);
	    	return "Aucun risque (Patient non trouvé)"; // Ou un autre statut d'erreur/inconnu si plus approprié.
	    }
	    logger.debug("Patient récupéré avec l'Id {}: {}", patientId, patient);

	    List<Transmission> listTransmission = patient.getTransmissionsList();
	    logger.debug("Transmissions récupérées pour le patient {} {}. Nombre de transmissions {}", patient.getNom(), patient.getPrenom(), listTransmission.size());


	    List<String> keywords = listMotCle.stream()
	    	    .map(String::toLowerCase)
	    	    .toList();

    	long totalKeywordOccurrences = listTransmission.stream()
    	    .map(Transmission::getTransmission)
    	    .map(String::toLowerCase)
    	    .flatMap(transmissionContent ->
    	        keywords.stream()
    	            .filter(transmissionContent::contains)
    	    )
    	    .count();

	    logger.debug("Nombre TOTAL de mots-clés déclencheurs trouvés dans toutes les transmissions : {}", totalKeywordOccurrences);

	    // 4. Récupération de l'âge et du genre du patient
	    int agePatient = patientService.agePatient(patient.getDateNaissance());
	    logger.debug("Le patient est âgé de : {}", agePatient);

	    String genrePatient = (patient.getGenre() != null) ? patient.getGenre() : "";
	    logger.debug("Genre du patient : {}", genrePatient);

	    // 5. Évaluation du niveau de risque (du plus critique au moins critique)
	    if (isEarlyOnset(totalKeywordOccurrences, agePatient, genrePatient)) {
	    	logger.info("Le patient {} est classé 'Apparition précoce' (Early Onset).", patientId);
	    	return "Early onset";
	    } else if (isInDanger(totalKeywordOccurrences, agePatient, genrePatient)) {
	    	logger.info("Le patient {} est classé 'Danger' (In Danger).", patientId);
	    	return "In Danger";
	    } else if (isBordline(totalKeywordOccurrences, agePatient)) {
	    	logger.info("Le patient {} est classé 'Risque limité' (Borderline).", patientId);
	    	return "Borderline";
	    } else {
	    	logger.info("Le patient {} ne présente aucun risque majeur (classé 'None').", patientId);
	    	return "None";
	    }
	}
	
    /**
     * Vérifie si le patient est en situation de risque limité (Borderline).
     *
     * @param occurence le nombre de mots-clés déclencheurs trouvés
     * @param age l’âge du patient
     * @return true si le patient est borderline, sinon false
     */
    public boolean isBordline(long occurence, int age) {
        // Le dossier du patient contient entre deux et cinq déclencheurs et le patient est âgé de plus de 30 ans.
    	if (occurence >= 2 && occurence <= 5 && age >= 30) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Vérifie si le patient est en situation de danger (In Danger).
     *
     * @param occurence le nombre de mots-clés déclencheurs trouvés
     * @param age l’âge du patient
     * @param genre le genre du patient ("masculin" ou "féminin")
     * @return true si le patient est en danger, sinon false
     */
    public boolean isInDanger(long occurence, int age, String genre) {
		// Homme < 30 ans : >= 3 déclencheurs
		if (age < 30 && "masculin".equalsIgnoreCase(genre) && occurence >= 3) {
			logger.info("ici");
			return true;
		}
		// Femme < 30 ans : >= 4 déclencheurs
		else if (age < 30 && "feminin".equalsIgnoreCase(genre) && occurence >= 4) {
			
			logger.info("le patient : age {}, genre : {}, occurence : {}", age, genre, occurence);
			return true;
		}
		// > 30 ans : 6 ou 7 déclencheurs
		else if (age >= 30 && (occurence == 6 || occurence == 7)) {
			logger.info("ici: 3");
			return true;
		}
		else {
			return false;
		}
    }
    
    /**
     * Vérifie si le patient est en situation d’apparition précoce (Early Onset).
     *
     * @param occurence le nombre de mots-clés déclencheurs trouvés
     * @param age l’âge du patient
     * @param genre le genre du patient
     * @return true si le patient est en early onset, sinon false
     */
    public boolean isEarlyOnset(long occurence, int age, String genre) {
    	logger.info("Tentative de calcul si la personne est : isEarlyOnSet");
    	logger.debug("le nombre d occurence : {}, l age : {}, le genre : {} ",occurence, age, genre);
    	
		// Homme < 30 ans : >= 5 déclencheurs
		if (age < 30 && "masculin".equalsIgnoreCase(genre) && occurence >= 5) {
			return true;
		}
		// Femme < 30 ans : >= 7 déclencheurs
		else if (age < 30 && "feminin".equalsIgnoreCase(genre) && occurence >= 7) {
			return true;
		}
		// > 30 ans : >= 8 déclencheurs
		else if (age >= 30 && occurence >= 8) {
			return true;
		}
		else {
			return false;
		}
    }

}
