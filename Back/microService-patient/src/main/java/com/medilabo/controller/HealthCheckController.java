package com.medilabo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur dédié à la vérification de l'état de santé du service patient.
 * 
 * Ce contrôleur expose une route simple permettant de vérifier que le microservice
 * est opérationnel. Il peut être utilisé par des outils d'orchestration, de supervision
 * ou simplement pour des vérifications manuelles.
 */
@RestController
@RequestMapping("/patient")
public class HealthCheckController {

    /**
     * Endpoint HTTP GET pour vérifier que le service patient est en bonne santé.
     * 
     * @return une réponse HTTP 200 avec le message "Patient service is healthy!" si tout fonctionne.
     */
	 @GetMapping("/health") // Ou n'importe quel autre chemin non utilisé
	 public ResponseEntity<String> healthCheck() {
	     return ResponseEntity.ok("Patient service is healthy!");
	 }
}