package com.medilabo.microService.alerte.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Note {

	String id;
	
	String patientId;
	
	String medecinId;
	
	LocalDateTime dateNote;
	
	private String nomMedecin;

	String prenomMedecin;

	private String note;

}
