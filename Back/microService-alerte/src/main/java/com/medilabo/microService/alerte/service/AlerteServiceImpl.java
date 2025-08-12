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
	    
	    int agePatient = patientClient.getAgePatient(patientId);
	    logger.debug("Le patient est âgé de : {}", agePatient);

	    String genrePatient = (patient.getGenre() != null) ? patient.getGenre() : "";
	    logger.debug("Genre du patient : {}", genrePatient);
	    
	    String riskDiabete = riskDiabete(listNotes, agePatient, genrePatient);
	    
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
	public String riskDiabete(List<Note> listNotes, int agePatient, String genrePatient) {
	    logger.info("Calcul du risque de diabète.");
	    List<String> keywords = listMotCle.stream()
	    	    .map(String::toLowerCase)
	    	    .toList();

    	long totalKeywordOccurrences = listNotes.stream()
    	    .map(Note::getNote)
    	    .map(String::toLowerCase)
    	    .flatMap(noteContent ->
    	        keywords.stream()
    	            .filter(noteContent::contains)
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
     * Détermine si le patient présente un risque limité (Borderline).
     * @param occurence Le nombre TOTAL de termes déclencheurs trouvés.
     * @param age L'âge du patient.
     * @return true si le risque est Borderline, false sinon.
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