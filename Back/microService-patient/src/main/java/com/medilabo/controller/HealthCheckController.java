package com.medilabo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

 @GetMapping("/health") // Ou n'importe quel autre chemin non utilisé
 public ResponseEntity<String> healthCheck() {
     return ResponseEntity.ok("Patient service is healthy!");
 }
}