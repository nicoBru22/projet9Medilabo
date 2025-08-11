package com.medilabo.microService.alerte.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medilabo.microService.alerte.feign.IPatientClient;
import com.medilabo.microService.alerte.feign.INoteClient;
import com.medilabo.microService.alerte.model.Note;
import com.medilabo.microService.alerte.model.Patient;

@Service
public class AlerteServiceImpl implements IAlerteService {

	/** La liste des declencheurs
	 * <p>
	 * La liste des termes déclencheurs à rechercher dans les notes du prestataire de santé.
	 * </p>
	 * 
	 */
	private List<String> listMotCle = List.of(
		    "HbA1C", "Microalbumine", "Taille", "Poids", "Fumeur", "Fumer",
		    "Fumeuse", "Anormal", "Cholestérol", "Vertiges", "Rechute", "Réaction", "Anticorps"
		);
	private Logger logger = LogManager.getLogger();

	@Autowired
    private INoteClient noteClient;

	@Autowired
	private IPatientClient patientClient;

	/**
	 * Évalue le niveau de risque de diabète pour un patient donné.
	 *
	 * @param patientId L'identifiant unique du patient.
	 * @return Le niveau de risque sous forme de chaîne de caractères (None, Borderline, In Danger, Early Onset).
	 */
	public String riskEvaluation (String patientId) {
		logger.info("Tentative de récupération de l'alerte santé pour le patient : {}", patientId);

	    List<Note> listNotes = noteClient.getAllNotesPatient(patientId);
	    logger.debug("Notes récupérées pour le patient {}: {}", patientId, listNotes);

	    Patient patient = patientClient.getPatientById(patientId);
	    if (patient == null) {
	    	logger.warn("Patient non trouvé avec l'ID : {}. Impossible d'évaluer le risque.", patientId);
	    	return "Aucun risque (Patient non trouvé)";
	    }
	    logger.debug("Patient récupéré avec l'Id {}: {}", patientId, patient);

	    long totalKeywordOccurrences = 0;
	    for (Note note : listNotes) {
	        String noteContent = note.getNote().toLowerCase();
	        for (String motCle : listMotCle) {
	            if (noteContent.contains(motCle.toLowerCase())) {
	                totalKeywordOccurrences++;
	            }
	        }
	    }
	    logger.debug("Nombre TOTAL de mots-clés déclencheurs trouvés dans toutes les transmissions : {}", totalKeywordOccurrences);

	    int agePatient = patientClient.getAgePatient(patientId);
	    logger.debug("Le patient est âgé de : {}", agePatient);

	    String genrePatient = (patient.getGenre() != null) ? patient.getGenre() : "";
	    logger.debug("Genre du patient : {}", genrePatient);

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
     * Détermine si le patient présente un risque limité (Borderline).
     * @param occurence Le nombre TOTAL de termes déclencheurs trouvés.
     * @param age L'âge du patient.
     * @return true si le risque est Borderline, false sinon.
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
     * Détermine si le patient présente un risque de danger (In Danger).
     * @param occurence Le nombre TOTAL de termes déclencheurs trouvés.
     * @param age L'âge du patient.
     * @param genre Le genre du patient ("masculin" ou "feminin").
     * @return true si le risque est In Danger, false sinon.
     */
    public boolean isInDanger(long occurence, int age, String genre) {
		// Homme < 30 ans : >= 3 déclencheurs
		if (age < 30 && "masculin".equalsIgnoreCase(genre) && occurence >= 3) {
			return true;
		}
		// Femme < 30 ans : >= 4 déclencheurs
		else if (age < 30 && "feminin".equalsIgnoreCase(genre) && occurence >= 4) {
			return true;
		}
		// > 30 ans : 6 ou 7 déclencheurs
		else if (age >= 30 && (occurence == 6 || occurence == 7)) {
			return true;
		}
		else {
			return false;
		}
    }

    /**
     * Détermine si le patient présente un risque d'apparition précoce (Early Onset).
     * @param occurence Le nombre TOTAL de termes déclencheurs trouvés.
     * @param age L'âge du patient.
     * @param genre Le genre du patient ("masculin" ou "feminin").
     * @return true si le risque est Early Onset, false sinon.
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