package com.medilabo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente un patient dans le système Medilabo.
 * 
 * Cette classe est un document MongoDB, persisté dans la collection "patient".
 * Elle contient les informations personnelles du patient, les métadonnées de création/modification,
 * ainsi que la liste de ses transmissions médicales.
 * 
 * <p>Les annotations Lombok {@code @Getter}, {@code @Setter}, {@code @NoArgsConstructor}
 * et {@code @AllArgsConstructor} sont utilisées pour générer automatiquement les accesseurs,
 * les constructeurs par défaut et complet.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="patient")
public class Patient {
	
    /**
     * Identifiant unique du patient (généré par MongoDB).
     */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
    /**
     * Nom du patient.
     * Ce champ est obligatoire.
     */
    @Column(nullable = false)
	@NotNull(message = "Le prénom est obligatoire.")
	@NotBlank(message = "Le prénom est obligatoire.")
	@Size(min = 3, message = "Le nom d'utilisateur doit contenir au moins 3 caractères.")
	@Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ '-]+$", message = "Le prénom ne doit contenir que des lettres.")
	String prenom;
	
    /**
     * Prenom du patient.
     * Ce champ est obligatoire.
     */
    @Column(nullable = false)
	@NotNull(message = "Le nom du patient est obligatoire.")
	@NotBlank(message = "Le nom du patient est obligatoire.")
	@Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ '-]+$", message = "Le nom ne doit contenir que des lettres.")
	String nom;
	
    /**
     * Date de naissance du patient.
     * Ce champ est obligatoire.
     */
    @Column(nullable = false)
	@NotNull(message = "La date de naissance est obligatoire.")
	LocalDate dateNaissance;
	
    /**
     * Genre du patient (ex : "M", "F").
     * Ce champ est obligatoire.
     */
    @Column(nullable = false)
	@NotNull(message = "Le genre est obligatoire.")
	@NotBlank(message = "Le genre est obligatoire.")
	String genre;
	
    /**
     * Adresse postale du patient (facultative).
     */
	@Column
	String adresse;
	
    /**
     * Numéro de téléphone du patient (facultatif).
     */
	@Column
	String telephone;
	
    /**
     * Date de création de l’enregistrement du patient.
     */
	@Column
	LocalDateTime dateCreation;
	
    /**
     * Date de la dernière modification des données du patient.
     */
	@Column
	LocalDateTime dateModification;
}
