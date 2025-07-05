package com.medilabo.microService.utilisateur.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
@Document(collection = "utilisateur")
public class User {
	
	@Id
	String id;
	
	@Indexed(unique = true)
	@NotNull(message = "le username est obligatoire.")
	String username;
	
	@NotNull(message = "le nom est obligatoire.")
	String nom;
	
	@NotNull(message = "le prenom est obligatoire.")
	String prenom;
	
	@NotNull(message = "le mot de passe est obligatoire.")
	String password;
	
	@NotNull(message = "le rôle est obligatoire.")
	String role;

}
