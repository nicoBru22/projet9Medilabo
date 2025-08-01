package com.medilabo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@Document(collection = "patient")
public class Patient {
	
	@Id
	String id;
	
	@NotNull(message = "Le prénom est obligatoire.")
	String prenom;
	
	@NotNull(message = "Le nom du patient est obligatoire.")
	String nom;
	
	@NotNull(message = "La date de naissance est obligatoire.")
	LocalDate dateNaissance;
	
	@NotNull(message = "Le genre est obligatoire.")
	String genre;
	
	String adresse;
	String telephone;
	LocalDateTime dateCreation;
	LocalDateTime dateModification;
	
	List<Transmission> transmissionsList = new ArrayList<>();

}
