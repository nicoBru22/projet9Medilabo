package com.medilabo.microService.alerte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MicroServiceAlerteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroServiceAlerteApplication.class, args);
	}

}
