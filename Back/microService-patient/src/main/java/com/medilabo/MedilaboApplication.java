package com.medilabo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

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
     * <p>
     * CHarge les variables d'environnement avant de lancer l'application.
     * </p>
     * 
     * @param args arguments de la ligne de commande (non utilisés ici)
     */
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
			    .directory(".")
			    .load();

			dotenv.entries()
			    .forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(MedilaboApplication.class, args);
	}

}
