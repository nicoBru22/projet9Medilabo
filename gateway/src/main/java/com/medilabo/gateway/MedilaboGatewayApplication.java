package com.medilabo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal de l'application Spring Boot pour la Gateway API Medilabo.
 * <p>
 * Cette classe démarre le contexte Spring et initialise la gateway qui centralise
 * la gestion des requêtes vers les différents microservices (utilisateur, patient, etc.).
 * </p>
 */
@SpringBootApplication
public class MedilaboGatewayApplication {

    /**
     * Méthode main qui lance l'application Spring Boot Gateway.
     * 
     * @param args arguments de la ligne de commande (non utilisés)
     */
	public static void main(String[] args) {
		SpringApplication.run(MedilaboGatewayApplication.class, args);
	}

}
