package com.medilabo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.medilabo.model.Patient;
import com.medilabo.model.Transmission;
import com.medilabo.service.IPatientService;

import jakarta.validation.Valid;


/**
 * Contrôleur REST permettant de gérer les opérations CRUD sur les patients
 * et leurs transmissions médicales.
 */
@RequestMapping("/patient")
@RestController
public class PatientController {

	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private IPatientService patientService;
	
    /**
     * Récupère la liste de tous les patients enregistrés.
     *
     * @return la liste des patients avec un code HTTP 200
     */
    @GetMapping("/list")
    public ResponseEntity<List<Patient>> getAllPatients() {
    	logger.info("Entrée dans le controller getAllPatients.");
        List<Patient> patientList = patientService.getAllPatient();
        logger.info("La liste des patients : {}", patientList);
        return ResponseEntity.ok(patientList);
    }
    
    /**
     * Calcule et retourne l'âge du patient à partir de sa date de naissance.
     *
     * @param id identifiant du patient
     * @return âge du patient
     * @throws ResponseStatusException si le patient est introuvable ou que sa date de naissance est absente
     */
    @GetMapping("/infos/{id}/age")
    public int getAgePatient(@PathVariable String id) {
        logger.info("Tentative de récupération de l'âge pour le patient avec l'ID : {}", id);
        Patient patient = patientService.getPatientById(id);

        if (patient == null) {
            logger.warn("Patient non trouvé avec l'ID : {}", id);
            // Renvoie un 404 si le patient n'est pas trouvé
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient with ID " + id + " not found.");
        }

        LocalDate dateNaissance = patient.getDateNaissance();

        // AJOUT DE CETTE VÉRIFICATION :
        if (dateNaissance == null) {
            logger.error("Date de naissance non trouvée (null) pour le patient avec l'ID : {}. Impossible de calculer l'âge.", id);
            // Il est important de renvoyer une erreur appropriée si la donnée est manquante.
            // Un HttpStatus.BAD_REQUEST (400) ou HttpStatus.UNPROCESSABLE_ENTITY (422) peut être plus pertinent.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date of birth is missing for patient with ID: " + id);
        }

        try {
            logger.info("Calcul de l'âge pour le patient {} avec date de naissance : {}", id, dateNaissance);
            // La méthode agePatient sera appelée ici avec une dateNaissance non-null
            int agePatient = patientService.agePatient(dateNaissance);
            logger.info("Âge du patient {} calculé : {}", id, agePatient);
            return agePatient;
        } catch (Exception e) {
            logger.error("Erreur inattendue lors du calcul de l'âge pour le patient {} : {}", id, e.getMessage(), e);
            // Renvoie une 500 en cas d'erreur de logique interne si la date n'est pas null
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calculating age for patient with ID: " + id, e);
        }
    }
	
    /**
     * Récupère les informations d'un patient selon son identifiant.
     *
     * @param id identifiant du patient
     * @return le patient correspondant
     */
	@GetMapping("/infos/{id}")
	public Patient getPatientById(@PathVariable String id) {
	    Patient patient = patientService.getPatientById(id);
        return patient;
	}
	
    /**
     * Ajoute un nouveau patient au système.
     *
     * @param patient objet Patient à enregistrer
     * @param result résultat de la validation
     * @return le patient ajouté ou une erreur 400 si les données sont invalides
     */
	@PostMapping("/add")
	public ResponseEntity<?> ajouterPatientApi(@RequestBody @Valid Patient patient, BindingResult result) {
	    logger.info("Entrée dans POST patient/add");
	    logger.info("Patient reçu : {}", patient);

	    if (result.hasErrors()) {
	    	logger.error("une erreur lors de l ajout du patient.");
	        result.getAllErrors().forEach(error -> logger.error(error.toString()));
	        return ResponseEntity.badRequest().body(result.getAllErrors());
	    }

	    Patient savedPatient = patientService.addPatient(patient);
	    return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
	}

    /**
     * Supprime un patient à partir de son identifiant.
     *
     * @param id identifiant du patient à supprimer
     * @return réponse HTTP 204 si la suppression est réussie
     */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deletePatient(@PathVariable("id") String id) {
	    logger.info("Entrée dans DELETE /delete/{} ", id);
	    patientService.deletePatient(id);
	    return ResponseEntity.noContent().build(); // 204 No Content
	}
	
    /**
     * Met à jour les informations d'un patient existant.
     *
     * @param id identifiant du patient à mettre à jour
     * @param patient données mises à jour
     * @param result résultat de la validation
     * @return le patient mis à jour ou une erreur si non trouvé ou invalide
     */
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updatePatient(
	        @PathVariable("id") String id,
	        @Valid @RequestBody Patient patient,
	        BindingResult result) {

	    if (result.hasErrors()) {
	        logger.error("Erreur lors de la mise à jour du patient : {}", result.getAllErrors());
	        return ResponseEntity.badRequest().body(result.getAllErrors());
	    }

	    Optional<Patient> updatedPatientOpt = patientService.updatePatient(patient);

	    if (updatedPatientOpt.isPresent()) {
	        logger.info("Mise à jour réussie du patient avec id {}", id);
	        return ResponseEntity.ok(updatedPatientOpt.get());
	    } else {
	        logger.warn("Patient non trouvé avec id {}", id);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Patient non trouvé avec id " + id);
	    }
	} 
	
    /**
     * Ajoute une nouvelle transmission médicale à un patient existant.
     *
     * @param newTransmission transmission à ajouter
     * @param patientId identifiant du patient concerné
     * @return la transmission créée
     */
	@PostMapping("/transmission/add")
	public ResponseEntity<Transmission> addTransmission(@Valid @RequestBody Transmission newTransmission, 
																@RequestParam String patientId) {
	    logger.info("Requête reçue pour ajouter une transmission : {}", newTransmission);
	    Transmission transmission = patientService.addTransmission(newTransmission, patientId);
	    logger.info("Transmission ajoutée avec succès : {}", transmission);
	    return ResponseEntity.status(HttpStatus.CREATED).body(transmission);
	}
	
    /**
     * Récupère toutes les transmissions associées à un patient donné.
     *
     * @param patientId identifiant du patient
     * @return la liste des transmissions médicales
     */
	@GetMapping("/transmission/liste")
	public ResponseEntity<List<Transmission>> getAllTransmissionOfPatient(@RequestParam String patientId) {
	    logger.info("Requête reçue pour récupérer la liste des transmissions du patient avec l'Id : {}", patientId);
	    List<Transmission> transmissionList = patientService.getAllTransmissionOfPatient(patientId);
	    logger.info("la liste de transmission récupérée : {}", transmissionList);
	    logger.info("Liste de transmission récupérée avec succès.");
	    return ResponseEntity.ok(transmissionList);
	}



}
