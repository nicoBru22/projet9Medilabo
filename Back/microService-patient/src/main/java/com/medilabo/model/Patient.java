package com.medilabo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "patient")
public class Patient {
	
    /**
     * Identifiant unique du patient (généré par MongoDB).
     */
	@Id
	String id;
	
    /**
     * Nom du patient.
     * Ce champ est obligatoire.
     */
	@NotNull(message = "Le prénom est obligatoire.")
	@NotBlank(message = "Le prénom est obligatoire.")
	@Size(min = 3, message = "Le nom d'utilisateur doit contenir au moins 3 caractères.")
	@Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ '-]+$", message = "Le prénom ne doit contenir que des lettres.")
	String prenom;
	
    /**
     * Prenom du patient.
     * Ce champ est obligatoire.
     */
	@NotNull(message = "Le nom du patient est obligatoire.")
	@NotBlank(message = "Le nom du patient est obligatoire.")
	@Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ '-]+$", message = "Le nom ne doit contenir que des lettres.")
	String nom;
	
    /**
     * Date de naissance du patient.
     * Ce champ est obligatoire.
     */
	@NotNull(message = "La date de naissance est obligatoire.")
	LocalDate dateNaissance;
	
    /**
     * Genre du patient (ex : "M", "F").
     * Ce champ est obligatoire.
     */
	@NotNull(message = "Le genre est obligatoire.")
	@NotBlank(message = "Le genre est obligatoire.")
	String genre;
	
    /**
     * Adresse postale du patient (facultative).
     */
	String adresse;
	
    /**
     * Numéro de téléphone du patient (facultatif).
     */
	String telephone;
	
    /**
     * Date de création de l’enregistrement du patient.
     */
	LocalDateTime dateCreation;
	
    /**
     * Date de la dernière modification des données du patient.
     */
	LocalDateTime dateModification;
	
    /**
     * Liste des transmissions médicales associées au patient.
     */
	List<Transmission> transmissionsList = new ArrayList<>();

}
