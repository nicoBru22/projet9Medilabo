package com.medilabo.microService.anticipation.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Transmission {

	String id;
	
	String patientId;
	
	LocalDateTime dateTransmission;
	
	private String profession;
	
	private String nomMedecin;

	String prenomMedecin;

	private String transmission;

}
