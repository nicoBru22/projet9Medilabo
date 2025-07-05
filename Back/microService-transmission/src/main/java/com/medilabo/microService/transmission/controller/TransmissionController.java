package com.medilabo.microService.transmission.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medilabo.microService.transmission.model.Transmission;
import com.medilabo.microService.transmission.service.ITransmissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transmission")
public class TransmissionController {
	private static final Logger logger = LogManager.getLogger(TransmissionController.class);

	@Autowired
	private ITransmissionService transmissionService;

	@PostMapping("/add")
	public ResponseEntity<?> addTransmission(@Valid @RequestBody Transmission newTransmission) {
		logger.info("Requête reçue pour ajouter une transmission : {}", newTransmission);

		try {
			Transmission transmission = transmissionService.addTransmission(newTransmission);
			logger.info("Transmission ajoutée avec succès : {}", transmission);
			return ResponseEntity.status(HttpStatus.CREATED).body(transmission);
		} catch (Exception e) {
			logger.error("Erreur lors de l'ajout de la transmission", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/getTransmissionsOfPatient")
	public ResponseEntity<List<Transmission>> getAllTransmissionOfPatient(@RequestParam String patientId) {
		logger.info("Requête reçue pour récupérer les transmissions du patient avec l'ID : {}", patientId);

		try {
			List<Transmission> transmissionList = transmissionService.getAllTransmissionsByPatientId(patientId);
			if (transmissionList.isEmpty()) {
				logger.warn("Aucune transmission trouvée pour le patient avec l'ID : {}", patientId);
			} else {
				logger.info("{} transmissions trouvées pour le patient {}", transmissionList.size(), patientId);
			}
			return ResponseEntity.status(HttpStatus.OK).body(transmissionList);
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des transmissions du patient : {}", patientId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
