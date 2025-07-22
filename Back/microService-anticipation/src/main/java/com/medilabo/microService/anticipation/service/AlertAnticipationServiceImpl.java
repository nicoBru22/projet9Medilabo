package com.medilabo.microService.anticipation.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medilabo.microService.anticipation.feign.IPatientClient;
import com.medilabo.microService.anticipation.feign.ITransmissionClient;
import com.medilabo.microService.anticipation.model.Patient;
import com.medilabo.microService.anticipation.model.Transmission;

@Service
public class AlertAnticipationServiceImpl implements IAlertAnticipationService {

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
    private ITransmissionClient transmissionClient;

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

		// 1. Récupération des transmissions du patient
	    List<Transmission> listTransmission = transmissionClient.getAllTransmissionOfPatient(patientId);
	    logger.debug("Transmissions récupérées pour le patient {}: {}", patientId, listTransmission);

	    // 2. Récupération des informations du patient
	    Patient patient = patientClient.getPatientById(patientId);
	    // Vérifie si le patient existe. Si non, impossible d'évaluer le risque.
	    if (patient == null) {
	    	logger.warn("Patient non trouvé avec l'ID : {}. Impossible d'évaluer le risque.", patientId);
	    	return "Aucun risque (Patient non trouvé)"; // Ou un autre statut d'erreur/inconnu si plus approprié.
	    }
	    logger.debug("Patient récupéré avec l'Id {}: {}", patientId, patient);

	    // 3. Comptage TOTAL des termes déclencheurs dans TOUTES les transmissions
	    long totalKeywordOccurrences = 0;
	    for (Transmission transmission : listTransmission) {
	        String transmissionContent = transmission.getTransmission().toLowerCase(); // Convertir une seule fois
	        for (String motCle : listMotCle) {
	            // Compter les occurrences de chaque mot-clé dans la transmission
	            // Ceci est une implémentation simple qui compte les correspondances exactes.
	            // Si un mot-clé apparaît plusieurs fois dans la même transmission, il sera compté plusieurs fois.
	            if (transmissionContent.contains(motCle.toLowerCase())) {
	                // Pour un comptage exact de toutes les occurrences d'un mot (ex: "fumeur fumeur" compte 2)
	                // il faudrait une logique plus complexe (regex ou boucle de indexOf).
	                // Pour l'instant, on compte si le mot-clé est présent au moins une fois par transmission pour chaque mot-clé.
	                // Si "Fumeur" apparaît 3 fois dans une note, et "Cholestérol" 1 fois,
	                // cette logique comptera 1 pour "Fumeur" et 1 pour "Cholestérol" dans cette note.
	                // Si vous voulez compter chaque apparition, même multiple dans une note, il faut affiner.
	                // Pour l'instant, je vais implémenter le comptage "est-ce que le mot-clé X est présent dans la note Y ?".
	                // Si vous voulez compter N fois si le mot apparaît N fois, dites-le moi.
	                totalKeywordOccurrences++;
	            }
	        }
	    }
	    logger.debug("Nombre TOTAL de mots-clés déclencheurs trouvés dans toutes les transmissions : {}", totalKeywordOccurrences);


	    // 4. Récupération de l'âge et du genre du patient
	    int agePatient = patientClient.getAgePatient(patientId);
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