package com.medilabo.microService.utilisateur.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@Entity
@Table(name = "utilisateur", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {
	
	/**
	 * Identifiant unique généré automatiquement par MongoDB.
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	/**
	 * Nom d'utilisateur unique utilisé pour l'authentification.
	 * Ne peut pas être null ou vide.
	 * Le username doit contenir entre 8 et 15 caractères.
	 */
    @Column(name = "username", nullable = false, unique = true)
	@Size(min=8, max=15, message = "le username doit contenir au moins 8 caractères et au maximum 15.")
	@NotBlank(message = "le username est obligatoire.")
	String username;
	
	/**
	 * Nom de l'utilisateur.
	 * Ne peut pas être null ou vide.
	 * Doit contenir seulement des lettres avec un tiret si nécessaire.
	 */
    @Column(name = "nom", nullable = false)
	@NotBlank(message = "le nom est obligatoire.")
	@Pattern(
			  regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ]+(-[A-Za-zÀ-ÖØ-öø-ÿ]+)?$",
			  message = "Seules les lettres sont autorisées, avec éventuellement un seul tiret entre deux mots."
			)
	String nom;
	
	/**
	 * Prénom de l'utilisateur.
	 * Ne peut pas être null ou vide.
	 * Doit contenir seulement des lettres avec un tiret si nécessaire.
	 */
    @Column(name = "prenom", nullable = false)
	@NotBlank(message = "le prénom est obligatoire.")
	@Pattern(
			  regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ]+(-[A-Za-zÀ-ÖØ-öø-ÿ]+)?$",
			  message = "Seules les lettres sont autorisées, avec éventuellement un seul tiret entre deux mots."
			)
	String prenom;
	
	/**
	 * Mot de passe de l'utilisateur.
	 * Ne peut pas être null ou vide.
	 * doit contenir au moins 8 caractères et au moins
	 * une majuscule, un chiffre et un symbole parmis !@#$%^&*
	 */
    @Column(name = "password", nullable = false)
	@NotBlank(message = "le mot de passe est obligatoire.")
	@Size(min=8, message = "le mot de passe doit contenir au minimum 8 caractères")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).*$", 
			message = "Le mot de passe doit contenir au moins une Majuscule, un chiffre, et un symbole parmis : !@#$%^&*.")
	String password;
	
	/**
	 * Rôle de l'utilisateur.
	 * Ne peut pas être null.
	 * Ne doit pas contenir de chiffre.
	 */
    @Column(nullable = false)
	@NotBlank(message = "le rôle est obligatoire.")
	@Pattern(
			  regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$",
			  message = "Le rôle ne doit contenir que des lettres, espaces, tirets ou apostrophes."
			)
	String role;

}
