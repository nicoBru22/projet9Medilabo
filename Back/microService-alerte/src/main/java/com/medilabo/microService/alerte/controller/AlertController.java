package com.medilabo.microService.alerte.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.microService.alerte.service.IAlerteService;

@RestController
@RequestMapping("/alerte")
public class AlertController {

	private Logger logger = LogManager.getLogger();
	
	@Autowired
    private IAlerteService alertService;


    @GetMapping("/detecte")
    public ResponseEntity<String> getAlert(@RequestParam Long patientId) {
    	logger.info("Entrée dans le controller alerte/detecte avec le patientId {}", patientId);
    	String alert = alertService.riskEvaluation(patientId);
    	logger.info("Récupération avec succès de l'alerte santé.");
        return ResponseEntity.ok(alert);
    }
}

