package com.medilabo.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.service.IAlerteService;

/**
 * Contrôleur REST chargé de l'évaluation du risque de diabète pour un patient donné.
 * 
 * Ce contrôleur expose un point d'entrée HTTP permettant d'évaluer l'état de santé
 * d'un patient à partir de son identifiant. Il utilise le service {@link IAlerteService}
 * pour analyser les données et retourner un niveau de risque.
 */
@RestController
@RequestMapping("/patient")
public class AlerteController {

    /**
     * Service permettant d’évaluer le risque de diabète en fonction des transmissions du patient.
     */
	@Autowired
	private IAlerteService alerteService;
	
    /**
     * Logger pour tracer l'exécution de la méthode.
     */
	private Logger logger = LogManager.getLogger();
	
    /**
     * Endpoint HTTP GET permettant de détecter une alerte de santé pour un patient donné.
     *
     * @param patientId L'identifiant unique du patient.
     * @return Une réponse contenant l'évaluation du risque (ex: "None", "Borderline", "In Danger", "Early onset").
     */
	@GetMapping("/diabete")
    public ResponseEntity<String> getAlert(@RequestParam String patientId) {
    	logger.info("Entrée dans le controller detecterAlerte avec le patientId {}", patientId);
    	String alert = alerteService.riskEvaluation(patientId);
    	logger.info("Récupération avec succès de l'alerte santé.");
        return ResponseEntity.ok(alert);
    }
}
