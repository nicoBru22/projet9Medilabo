package com.medilabo.microService.utilisateur.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/utilisateur")
public class HealthCheckController {

 @GetMapping("/health")
 public ResponseEntity<String> healthCheck() {
     return ResponseEntity.ok("Patient service is healthy!");
 }
}