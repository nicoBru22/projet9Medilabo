package com.medilabo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application Medilabo.
 * 
 * Cette classe configure et lance l'application Spring Boot.
 * Le démarrage s'effectue via la méthode main, qui appelle SpringApplication.run().
 * 
 * L'annotation {@link SpringBootApplication} indique que c'est une application Spring Boot
 * avec configuration automatique et scan des composants.
 */
@SpringBootApplication
public class MedilaboApplication {

    /**
     * Point d'entrée principal de l'application.
     * 
     * @param args arguments de la ligne de commande (non utilisés ici)
     */
	public static void main(String[] args) {
		SpringApplication.run(MedilaboApplication.class, args);
	}

}
