package com.medilabo.microService.anticipation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MicroServiceAnticipationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroServiceAnticipationApplication.class, args);
	}

}
