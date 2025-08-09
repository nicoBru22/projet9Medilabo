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
	 * Évalue le risque médical d’un patient en analysant ses transmissions et ses données personnelles.
	 * <p>
	 * Cette méthode récupère le patient via son identifiant, puis analyse l'ensemble
	 * de ses transmissions et ses données démographiques (âge, genre) afin de déterminer
	 * un niveau de risque lié à une maladie. Actuellement, seulement le diabète.
	 * </p>
	 *
	 * @param patientId l'identifiant unique du patient à analyser
	 * @return une chaîne indiquant le niveau de risque détecté, ou
	 *         "Aucun risque (Patient non trouvé)" si l'identifiant ne correspond à aucun patient
	 */
	public String riskEvaluation (String patientId) {
		logger.info("Tentative de récupération de l'alerte santé pour le patient : {}", patientId);
		
		Patient patient = patientService.getPatientById(patientId);
	    logger.debug("Patient récupéré avec l'Id {}: {}", patientId, patient);
	    
	    if (patient == null) {
	    	logger.warn("Patient non trouvé avec l'ID : {}. Impossible d'évaluer le risque.", patientId);
	    	return "Aucun risque (Patient non trouvé)";
	    }

	    List<Transmission> listTransmission = patient.getTransmissionsList();
	    logger.debug("Transmissions récupérées pour le patient {} {}. "
	    		+ "Nombre de transmissions {}", patient.getNom(), patient.getPrenom(), listTransmission.size());

	    int agePatient = patientService.agePatient(patient.getDateNaissance());
	    logger.debug("Le patient est âgé de : {}", agePatient);

	    String genrePatient = (patient.getGenre() != null) ? patient.getGenre() : "";
	    logger.debug("Genre du patient : {}", genrePatient);

	    String riskDiabete = riskDiabete(listTransmission, agePatient, genrePatient);
	    
	    return riskDiabete;

	}
	
	/**
	 * Calcule le risque de diabète d’un patient en fonction des transmissions médicales, de l’âge et du genre.
	 * <p>
	 * Cette méthode compte le nombre d’occurrences de mots-clés spécifiques aux facteurs de risque
	 * dans l’ensemble des transmissions du patient. Selon le nombre d’occurrences et les critères
	 * d’âge/genre, elle détermine un niveau de risque parmi les valeurs :
	 * </p>
	 *
	 * @param listTransmission la liste des transmissions médicales du patient
	 * @param agePatient       l'âge du patient en années
	 * @param genrePatient     le genre du patient
	 * @return une chaîne indiquant le niveau de risque détecté
	 */
	public String riskDiabete(List<Transmission> listTransmission, int agePatient, String genrePatient) {
	    logger.info("Calcul du risque de diabète.");
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

	    logger.debug("Nombre TOTAL de mots-clés déclencheurs trouvés dans toutes les "
	    		+ "transmissions : {}", totalKeywordOccurrences);
		
		if (isEarlyOnset(totalKeywordOccurrences, agePatient, genrePatient)) {
	    	logger.info("Le patient est classé 'Apparition précoce' (Early Onset).");
	    	return "Early onset";
	    } else if (isInDanger(totalKeywordOccurrences, agePatient, genrePatient)) {
	    	logger.info("Le patient est classé 'Danger' (In Danger).");
	    	return "In Danger";
	    } else if (isBorderline(totalKeywordOccurrences, agePatient)) {
	    	logger.info("Le patient est classé 'Risque limité' (Borderline).");
	    	return "Borderline";
	    } else {
	    	logger.info("Le patient ne présente aucun risque majeur (classé 'None').");
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
    public boolean isBorderline(long occurence, int age) {
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
