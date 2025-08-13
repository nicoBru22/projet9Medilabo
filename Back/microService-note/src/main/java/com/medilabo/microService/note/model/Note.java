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
@Document(collection = "note")
public class Note {

	@Id
	String id;
	
	Long patientId;
	
	Medecin medecin;
	
	LocalDateTime dateNote;

	@NotBlank(message = "La transmission ne peut pas être vide")
	private String note;

}
