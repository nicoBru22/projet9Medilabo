package com.medilabo.microservice_rdv.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.medilabo.microservice_rdv.model.Rdv;
import com.medilabo.microservice_rdv.service.RdvService;

import jakarta.validation.Valid;

public class RdvController {
	
	private Logger logger = LogManager.getLogger();

	@Autowired
	private RdvService rdvService;
	
	@PostMapping("/rdv/add")
	public ResponseEntity<?> addRdvPatient(@Valid @RequestBody Rdv newRdv, BindingResult result ) {
		logger.info("Requête reçue pour ajouter un rendez vous au patient. {}", newRdv);
		
	    if (result.hasErrors()) {
	        Map<String, String> errors = new HashMap<>();
	        result.getFieldErrors().forEach(error -> {
	            errors.put(error.getField(), error.getDefaultMessage());
	            logger.error("Erreur sur le champ {} : {}", error.getField(), error.getDefaultMessage());
	        });
	        return ResponseEntity.badRequest().body(errors);
	    }
	    
		Rdv rdvAdded = rdvService.addRdv(newRdv);
	    logger.info("Ajout du rendez-vous réalisé avec succès.");

	    return ResponseEntity.status(HttpStatus.CREATED).body(rdvAdded);
		
	}
}
