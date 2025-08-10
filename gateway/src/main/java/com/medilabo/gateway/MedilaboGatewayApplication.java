package com.medilabo.gateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Point d'entrée principal de l'application Spring Boot pour la Gateway API Medilabo.
 * <p>
 * Cette classe démarre le contexte Spring et initialise la gateway qui centralise
 * la gestion des requêtes vers les différents microservices (utilisateur, patient, etc.).
 * </p>
 */
@SpringBootApplication
public class MedilaboGatewayApplication {
	
	private static Logger logger = LogManager.getLogger();
	
	static boolean isCI = "true".equals(System.getenv("CI"));

    /**
     * Méthode main qui lance l'application Spring Boot Gateway.
     * 
     * @param args arguments de la ligne de commande (non utilisés)
     */
	public static void main(String[] args) {
		
		if(!isCI) {
			Dotenv dotenv = Dotenv.configure()
				    .directory(".")
				    .ignoreIfMissing()
				    .load();

				String jwtSecret = dotenv.get("JWT_SECRET");
				if (jwtSecret != null && !jwtSecret.isEmpty()) {
				    logger.info("JWT_SECRET est bien chargé (longueur : {} caractères)", jwtSecret.length());
				} else {
				    logger.error("JWT_SECRET n'est PAS chargé !");
				}

				dotenv.entries()
				    .forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		}

		SpringApplication.run(MedilaboGatewayApplication.class, args);
	}

}
