package com.medilabo.microService.utilisateur.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente un utilisateur dans le système.
 * <p>
 * Cette classe est mappée à la collection MongoDB "utilisateur".
 * Elle contient les informations essentielles d'un utilisateur telles que
 * l'identifiant, le nom d'utilisateur unique, le nom, le prénom, le mot de passe
 * et le rôle attribué à l'utilisateur.
 * </p>
 * 
 * <p>Les validations sont appliquées via les annotations Jakarta Validation.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "utilisateur")
public class User {
	
	/**
	 * Identifiant unique généré automatiquement par MongoDB.
	 */
	@Id
	String id;
	
	/**
	 * Nom d'utilisateur unique utilisé pour l'authentification.
	 * Ne peut pas être null.
	 */
	@Indexed(unique = true)
	@NotNull(message = "le username est obligatoire.")
	String username;
	
	/**
	 * Nom de l'utilisateur.
	 * Ne peut pas être null.
	 */
	@NotNull(message = "le nom est obligatoire.")
	String nom;
	
	/**
	 * Prénom de l'utilisateur.
	 * Ne peut pas être null.
	 */
	@NotNull(message = "le prenom est obligatoire.")
	String prenom;
	
	/**
	 * Mot de passe de l'utilisateur.
	 * Ne peut pas être null.
	 */
	@NotNull(message = "le mot de passe est obligatoire.")
	String password;
	
	/**
	 * Rôle de l'utilisateur (ex : ROLE_ADMIN, ROLE_USER).
	 * Ne peut pas être null.
	 */
	@NotNull(message = "le rôle est obligatoire.")
	String role;

}
