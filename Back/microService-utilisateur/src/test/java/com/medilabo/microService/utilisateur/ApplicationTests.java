package com.medilabo.microService.utilisateur;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {
	
	private static Logger logger = LogManager.getLogger();
	
	@BeforeAll
	static void setup() {
	    boolean isCI = "true".equals(System.getenv("CI"));
	    if (!isCI) {
	        Dotenv dotenv = Dotenv.configure()
	                .directory(".")
	                .ignoreIfMissing()
	                .load();

	        String jwtSecret = dotenv.get("JWT_SECRET");

	        if (jwtSecret != null && !jwtSecret.isEmpty()) {
	            System.setProperty("JWT_SECRET", jwtSecret);
	            logger.info("✅ JWT_SECRET chargé depuis .env dans ApplicationTests");
	        } else {
	            logger.error("❌ JWT_SECRET n'a pas été trouvé dans le fichier .env.");
	        }
	    } else {
	        String jwtSecret = System.getenv("JWT_SECRET");
	        if (jwtSecret != null && !jwtSecret.isEmpty()) {
	            System.setProperty("JWT_SECRET", jwtSecret);
	            logger.info("✅ JWT_SECRET chargé depuis System.getenv dans ApplicationTests");
	        } else {
	            logger.error("❌ JWT_SECRET n'est PAS défini dans les variables d'environnement CI !");
	        }
	    }
	}

	@Test
	void contextLoads() {
	}

}
