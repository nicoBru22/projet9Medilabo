package com.medilabo.microService.note.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transmission")
public class Note {

	@Id
	String id;
	
	String patientId;
	
	String medecinId;
	
	LocalDateTime dateTransmission;
	
	@NotBlank(message = "Le nom du médecin est obligatoire.")
	private String nomMedecin;

	@NotBlank(message = "Le prenom du médecin est obligatoire.")
	String prenomMedecin;

	@NotBlank(message = "La transmission ne peut pas être vide")
	private String note;

}
