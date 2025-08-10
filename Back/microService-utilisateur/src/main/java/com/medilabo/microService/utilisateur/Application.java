package com.medilabo.microService.utilisateur;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Classe principale de l'application Spring Boot "utilisateur".
 * 
 * Cette classe contient le point d'entrée de l'application.
 * Elle démarre le contexte Spring et le serveur web embarqué.
 */
@SpringBootApplication
public class Application {
	
	static Logger logger = LogManager.getLogger();
	
    /**
     * Point d'entrée de l'application Spring Boot.
     * 
     * @param args arguments de la ligne de commande (non utilisés)
     */
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
			    .directory(".")
			    .load();

			String jwtSecret = dotenv.get("JWT_SECRET");
			if (jwtSecret != null && !jwtSecret.isEmpty()) {
			    logger.info("✅ JWT_SECRET est bien chargé (longueur : {} caractères)", jwtSecret.length());
			} else {
			    logger.error("❌ JWT_SECRET n'est PAS chargé !");
			}

			dotenv.entries()
			    .forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        
		SpringApplication.run(Application.class, args);
	}

}
