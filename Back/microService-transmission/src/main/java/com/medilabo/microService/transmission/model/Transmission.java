package com.medilabo.microService.transmission.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transmission")
public class Transmission {

	@Id
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
