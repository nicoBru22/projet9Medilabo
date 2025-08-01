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

@RestController
@RequestMapping("/patient")
public class AlerteController {

	@Autowired
	private IAlerteService alerteService;
	
	private Logger logger = LogManager.getLogger();
	
	@GetMapping("/diabete")
    public ResponseEntity<String> getAlert(@RequestParam String patientId) {
    	logger.info("Entrée dans le controller detecterAlerte avec le patientId {}", patientId);
    	String alert = alerteService.riskEvaluation(patientId);
    	logger.info("Récupération avec succès de l'alerte santé.");
        return ResponseEntity.ok(alert);
    }
}
