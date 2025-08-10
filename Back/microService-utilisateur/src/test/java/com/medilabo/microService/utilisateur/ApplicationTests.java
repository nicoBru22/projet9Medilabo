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
        Dotenv dotenv = Dotenv.load();
        String jwtSecret = dotenv.get("JWT_SECRET");

        if (jwtSecret != null) {
            System.setProperty("JWT_SECRET", jwtSecret);
        } else {
            logger.error("Avertissement : JWT_SECRET n'a pas été trouvé dans le fichier .env.");
        }
    }

	@Test
	void contextLoads() {
	}

}
