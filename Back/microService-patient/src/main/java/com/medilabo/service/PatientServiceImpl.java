package com.medilabo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

import com.medilabo.model.Patient;
import com.medilabo.repository.IPatientRepository;

/**
 * Implémentation de l'interface {@link IPatientService}, fournissant
 * les opérations de gestion des patients et de leurs transmissions médicales.
 */
@Service
public class PatientServiceImpl implements IPatientService{
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private IPatientRepository patientRepository;
	
    /**
     * Récupère la liste de tous les patients.
     *
     * @return la liste des patients.
     */
	public List<Patient> getAllPatient() {
	    logger.info("Récupération de tous les patients.");
	    List<Patient> listePatient = patientRepository.findAll();
	    logger.info("{} patients récupérés.", listePatient.size());
	    return listePatient;
	}

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id l'identifiant du patient.
     * @return le patient correspondant.
     * @throws IllegalArgumentException si l'id est nul ou vide.
     * @throws NoSuchElementException si aucun patient n'est trouvé.
     */
	public Patient getPatientById(Long id) {
	    logger.info("Récupération du patient avec l'id : {}", id);
	    if (id == null) {
	        logger.error("L'id ne peut pas être null ou vide : {}", id);
	        throw new IllegalArgumentException("L'id ne peut pas être null ou vide");
	    }

	    Patient patient = patientRepository.findById(id)
	        .orElseThrow(() -> {
	            logger.error("Patient non trouvé avec l'id : {}", id);
	            return new NoSuchElementException("Patient non trouvé avec l'id : " + id);
	        });

	    logger.info("Patient récupéré avec succès : {}", patient);
	    return patient;
	}

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id l'identifiant du patient.
     * @throws IllegalArgumentException si l'id est nul ou vide.
     * @throws NoSuchElementException si aucun patient n'est trouvé.
     */
	public void deletePatient(Long id) {
	    logger.info("Appel à deletePatient avec id = {}", id);
	    if (id == null) {
	        logger.error("L'id ne peut pas être null ou vide : {}", id);
	        throw new IllegalArgumentException("L'id ne peut pas être null ou vide");
	    }
	    
	    if (!patientRepository.existsById(id)) {
	        logger.warn("Suppression impossible : patient non trouvé avec l'id : {}", id);
	        throw new NoSuchElementException("Patient non trouvé avec l'id : " + id);
	    }
	    
	    patientRepository.deleteById(id);
	    logger.info("Patient supprimé avec succès pour id = {}", id);
	}


    /**
     * Ajoute un nouveau patient dans le système.
     *
     * @param newPatient le patient à ajouter.
     * @return le patient ajouté.
     * @throws IllegalArgumentException si le patient est nul ou son nom est vide.
     */
    public Patient addPatient(Patient newPatient) {
        logger.info("Tentative d'ajout d'un nouveau patient : {}", newPatient);

        if (newPatient == null) {
            logger.error("Le patient à ajouter est null");
            throw new IllegalArgumentException("Le patient à ajouter ne peut pas être null");
        }

        if (newPatient.getNom() == null || newPatient.getNom().isBlank()) {
            logger.error("Le nom du patient est obligatoire");
            throw new IllegalArgumentException("Le nom du patient est obligatoire");
        }

        newPatient.setDateCreation(LocalDateTime.now());
        newPatient.setDateModification(LocalDateTime.now());

        Patient patientAjoute = patientRepository.save(newPatient);
        logger.info("Patient ajouté avec succès : {}", patientAjoute);

        return patientAjoute;
    }

    /**
     * Met à jour les informations d’un patient existant.
     *
     * @param patient le patient à mettre à jour.
     * @return un Optional contenant le patient mis à jour.
     * @throws IllegalArgumentException si le patient ou son id est null ou vide.
     * @throws RuntimeException si le patient n'existe pas.
     */
    public Optional<Patient> updatePatient(Patient patient) {
        logger.info("Tentative de mise à jour du patient avec l'id : {}", patient.getId());

        if (patient == null || patient.getId() == null) {
            logger.error("Patient ou id du patient est null ou vide");
            throw new IllegalArgumentException("Le patient ou son id ne peut pas être null ou vide");
        }

        Optional<Patient> patientExiste = patientRepository.findById(patient.getId());

        if (patientExiste.isPresent()) {
            Patient patientToUpdate = patientExiste.get();
            patientToUpdate.setNom(patient.getNom());
            patientToUpdate.setPrenom(patient.getPrenom());
            patientToUpdate.setAdresse(patient.getAdresse());
            patientToUpdate.setTelephone(patient.getTelephone());
            patientToUpdate.setGenre(patient.getGenre());
            patientToUpdate.setDateNaissance(patient.getDateNaissance());
            patientToUpdate.setDateModification(LocalDateTime.now());

            Patient patientMisAJour = patientRepository.save(patientToUpdate);
            logger.info("Patient mis à jour avec succès : {}", patientMisAJour);

            return Optional.of(patientMisAJour);
        } else {
            logger.error("Le patient n'existe pas avec l'Id : {}", patient.getId());
            throw new RuntimeException("Le patient n'existe pas avec l'Id " + patient.getId());
        }
    }

    /**
     * Calcule l'âge d’un patient à partir de sa date de naissance.
     *
     * @param dateNaissance la date de naissance.
     * @return l'âge en années.
     * @throws IllegalArgumentException si la date de naissance est null.
     */
    public int agePatient(LocalDate dateNaissance) {
    	logger.info("Tentative de calcul de l'age du patient à partir de sa date de naissance.");
        if (dateNaissance == null) {
            logger.error("La date de naissance ne peut pas être null");
            throw new IllegalArgumentException("La date de naissance ne peut pas être null");
        }
        LocalDate aujourdHui = LocalDate.now();
        Period period = Period.between(dateNaissance, aujourdHui);
        int age = period.getYears();
        logger.debug("Calcul de l'âge : dateNaissance = {}, age = {}", dateNaissance, age);
        return age;
    }

    


}
