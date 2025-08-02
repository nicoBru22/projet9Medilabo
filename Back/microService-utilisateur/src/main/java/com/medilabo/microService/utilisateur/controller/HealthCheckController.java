package com.medilabo.microService.utilisateur.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour vérifier la santé du service utilisateur.
 * <p>
 * Fournit un endpoint simple permettant de vérifier que le service est opérationnel.
 * </p>
 */
@RestController
@RequestMapping("/utilisateur")
public class HealthCheckController {

    /**
     * Endpoint de vérification de l'état de santé du service.
     * 
     * @return ResponseEntity contenant un message indiquant que le service est opérationnel avec un code HTTP 200 OK.
     */
	 @GetMapping("/health")
	 public ResponseEntity<String> healthCheck() {
	     return ResponseEntity.ok("Patient service is healthy!");
	 }
}