package com.medilabo.gateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class GatewayApplicationTests {
	
	private static Logger logger = LogManager.getLogger();
	
	@BeforeAll
	private static void setup() {
		Dotenv dotenv = Dotenv.configure()
			    .directory(".")
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

	@Test
	void contextLoads() {
	}

}
