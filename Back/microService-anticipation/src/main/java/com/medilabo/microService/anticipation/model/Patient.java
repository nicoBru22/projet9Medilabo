package com.medilabo.microService.anticipation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Patient {
	
	String id;
	String prenom;
	String nom;
	LocalDate dateNaissance;
	String genre;
	
	String adresse;
	String telephone;
	LocalDateTime dateCreation;
	LocalDateTime dateModification;
}
