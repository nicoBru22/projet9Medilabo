package com.medilabo.service;

import java.util.List;

import com.medilabo.model.Transmission;

/**
 * Interface définissant les services liés à l'évaluation du risque de diabète
 * pour un patient à partir de ses transmissions médicales.
 */
public interface IAlerteService {
	
    /**
     * Évalue le niveau de risque de diabète pour un patient à partir de son ID.
     * Cette évaluation repose sur le nombre de mots-clés présents dans les transmissions
     * médicales et les caractéristiques du patient (âge, genre).
     *
     * @param patientId l'identifiant du patient
     * @return un niveau de risque sous forme de chaîne de caractères :
     *         "None", "Borderline", "In Danger" ou "Early onset"
     */
	String riskEvaluation (String patientId);
	
	String riskDiabete(List<Transmission> listTransmission, int agePatient, String genrePatient);
	
    /**
     * Détermine si le patient est classé en risque "Borderline".
     * Un risque est "Borderline" si le patient a entre 2 et 5 déclencheurs
     * et est âgé de 30 ans ou plus.
     *
     * @param occurence le nombre de mots-clés déclencheurs trouvés
     * @param age l'âge du patient
     * @return {@code true} si les conditions sont remplies, {@code false} sinon
     */
	boolean isBorderline(long occurence, int age);
	
    /**
     * Détermine si le patient est classé "In Danger".
     * <ul>
     *   <li>Homme de moins de 30 ans : au moins 3 déclencheurs</li>
     *   <li>Femme de moins de 30 ans : au moins 4 déclencheurs</li>
     *   <li>Tout patient de 30 ans ou plus : 6 ou 7 déclencheurs</li>
     * </ul>
     *
     * @param occurence le nombre de mots-clés déclencheurs trouvés
     * @param age l'âge du patient
     * @param genre le genre du patient ("masculin", "féminin", etc.)
     * @return {@code true} si les conditions sont remplies, {@code false} sinon
     */
	boolean isInDanger(long occurence, int age, String genre);
	
    /**
     * Détermine si le patient est classé "Early Onset" (apparition précoce).
     * <ul>
     *   <li>Homme de moins de 30 ans : au moins 5 déclencheurs</li>
     *   <li>Femme de moins de 30 ans : au moins 7 déclencheurs</li>
     *   <li>Tout patient de 30 ans ou plus : au moins 8 déclencheurs</li>
     * </ul>
     *
     * @param occurence le nombre de mots-clés déclencheurs trouvés
     * @param age l'âge du patient
     * @param genre le genre du patient ("masculin", "féminin", etc.)
     * @return {@code true} si les conditions sont remplies, {@code false} sinon
     */
	boolean isEarlyOnset(long occurence, int age, String genre);

}
