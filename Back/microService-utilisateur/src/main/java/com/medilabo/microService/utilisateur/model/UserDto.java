package com.medilabo.microService.utilisateur.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) représentant les informations
 * d'identification nécessaires pour la connexion d'un utilisateur.
 * <p>
 * Contient uniquement le nom d'utilisateur et le mot de passe.
 * </p>
 * 
 * Utilisé pour transférer les données de connexion entre
 * les couches de l'application sans exposer l'objet complet User.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	
    /**
     * Nom d'utilisateur.
     */
	@NotBlank(message= "Le username de peut pas être vide")
	String username;
	
    /**
     * Mot de passe de l'utilisateur.
     */
	@NotBlank(message="Le mot de passe ne peut pas être vide")
	String password;

}
