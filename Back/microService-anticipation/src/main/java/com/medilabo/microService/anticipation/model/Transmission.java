package com.medilabo.microService.anticipation.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transmission {

	String id;
	
	String patientId;
	
	LocalDateTime dateTransmission;
	
	private String profession;
	
	private String nomMedecin;

	String prenomMedecin;

	private String transmission;

}
