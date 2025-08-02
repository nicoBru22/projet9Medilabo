package com.medilabo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.medilabo.model.Patient;
import com.medilabo.model.Transmission;

/**
 * Interface définissant les opérations disponibles pour la gestion des patients
 * et de leurs transmissions dans l'application Medilabo.
 */
public interface IPatientService {
	
    /**
     * Récupère la liste de tous les patients enregistrés.
     *
     * @return une liste de tous les patients
     */
	List<Patient> getAllPatient();
	
    /**
     * Récupère un patient par son identifiant unique.
     *
     * @param id l'identifiant du patient
     * @return le patient correspondant
     * @throws IllegalArgumentException si l'identifiant est null ou vide
     * @throws java.util.NoSuchElementException si aucun patient n'est trouvé avec cet identifiant
     */
	Patient getPatientById(String id);
	
	   /**
     * Supprime un patient à partir de son identifiant.
     *
     * @param id l'identifiant du patient à supprimer
     * @throws IllegalArgumentException si l'identifiant est null ou vide
     * @throws java.util.NoSuchElementException si le patient n'existe pas
     */
	void deletePatient(String id);
	
    /**
     * Ajoute un nouveau patient à la base de données.
     *
     * @param patient le patient à ajouter
     * @return le patient ajouté avec ses informations complétées
     * @throws IllegalArgumentException si le patient est null ou si des champs obligatoires sont manquants
     */
	Patient addPatient(Patient patient);
	
    /**
     * Met à jour les informations d'un patient existant.
     *
     * @param patient le patient avec les nouvelles informations
     * @return un Optional contenant le patient mis à jour s'il existe
     * @throws IllegalArgumentException si le patient ou son identifiant est null ou vide
     * @throws RuntimeException si le patient n'existe pas
     */
	Optional<Patient> updatePatient(Patient patient);
	
    /**
     * Calcule l'âge d'un patient à partir de sa date de naissance.
     *
     * @param dateNaissance la date de naissance du patient
     * @return l'âge en années
     * @throws IllegalArgumentException si la date de naissance est null
     */
	int agePatient(LocalDate dateNaissance);
	
    /**
     * Ajoute une nouvelle transmission pour un patient existant.
     *
     * @param newTransmission la transmission à ajouter
     * @param patientid l'identifiant du patient concerné
     * @return la transmission ajoutée avec son identifiant généré
     * @throws IllegalArgumentException si les champs obligatoires sont manquants ou si l'id est null
     * @throws java.util.NoSuchElementException si le patient n'existe pas
     */
	Transmission addTransmission(Transmission newTransmission, String patientid);
	
    /**
     * Récupère toutes les transmissions associées à un patient.
     *
     * @param patientId l'identifiant du patient
     * @return la liste des transmissions, vide si aucune n'existe
     * @throws IllegalArgumentException si l'identifiant est null ou vide
     * @throws java.util.NoSuchElementException si le patient n'existe pas
     */
	List<Transmission> getAllTransmissionOfPatient(String patientId);
}
