package com.medilabo.microService.utilisateur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application Spring Boot "utilisateur".
 * 
 * Cette classe contient le point d'entrée de l'application.
 * Elle démarre le contexte Spring et le serveur web embarqué.
 */
@SpringBootApplication
public class Application {

    /**
     * Point d'entrée de l'application Spring Boot.
     * 
     * @param args arguments de la ligne de commande (non utilisés)
     */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
