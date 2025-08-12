package com.medilabo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@ActiveProfiles("test")
class MedilaboApplicationTests {
	private static Logger logger = LogManager.getLogger();
	
	@BeforeAll
	static void setup() {
	    boolean isCI = "true".equals(System.getenv("CI"));
	    String envVarName = "SPRING_DATA_POSTGRE_URI";

	    if (!isCI) {
	        Dotenv dotenv = Dotenv.configure()
	                .directory(".")
	                .ignoreIfMissing()
	                .load();

	        String postgreUri = dotenv.get(envVarName);

	        if (postgreUri != null && !postgreUri.isEmpty()) {
	            System.setProperty(envVarName, postgreUri);
	            logger.info(envVarName + " chargé depuis .env dans ApplicationTests");
	        } else {
	            logger.error(envVarName + " n'a pas été trouvé dans le fichier .env.");
	        }
	    } else {
	        String postgreUri = System.getenv(envVarName);
	        if (postgreUri != null && !postgreUri.isEmpty()) {
	            System.setProperty(envVarName, postgreUri);
	            logger.info(envVarName + " chargé depuis System.getenv dans ApplicationTests");
	        } else {
	            logger.error(envVarName + " n'est PAS défini dans les variables d'environnement CI !");
	        }
	    }
	}

	@Test
	void contextLoads() {
	}

}
