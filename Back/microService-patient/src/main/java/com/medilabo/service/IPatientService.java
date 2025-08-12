package com.medilabo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.medilabo.model.Patient;
import com.medilabo.model.Rdv;

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
	Patient getPatientById(Long id);
	
	   /**
     * Supprime un patient à partir de son identifiant.
     *
     * @param id l'identifiant du patient à supprimer
     * @throws IllegalArgumentException si l'identifiant est null ou vide
     * @throws java.util.NoSuchElementException si le patient n'existe pas
     */
	void deletePatient(Long id);
	
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
	
	Rdv addRdv(Rdv newRdv);
}
