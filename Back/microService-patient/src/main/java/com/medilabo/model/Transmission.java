package com.medilabo.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transmission {

	String id;
	
	String patientId;
	
	LocalDateTime dateTransmission;
	
	private String profession;
	
	@NotNull(message = "Le nom du médecin est obligatoire.")
	private String nomMedecin;

	@NotNull(message = "Le prenom du médecin est obligatoire.")
	String prenomMedecin;

	@NotNull(message = "La transmission ne peut pas être vide")
	private String transmission;

}
