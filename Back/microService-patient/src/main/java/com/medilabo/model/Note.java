package com.medilabo.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente une transmission médicale liée à un patient.
 * 
 * Cette classe contient les informations concernant une transmission effectuée par un professionnel de santé,
 * avec la date, l'identifiant du patient concerné, ainsi que le contenu de la transmission.
 * 
 * <p>Les annotations Lombok {@code @Getter}, {@code @Setter}, {@code @NoArgsConstructor}
 * et {@code @AllArgsConstructor} permettent de générer automatiquement les accesseurs,
 * les constructeurs par défaut et complet.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Note {

    /**
     * Identifiant unique de la transmission.
     */
    String id;

    /**
     * Identifiant du patient concerné par la transmission.
     */
    Long patientId;
    
    Long medecinId;

    /**
     * Date et heure de la transmission.
     */
    LocalDateTime dateTransmission;

    /**
     * Nom du médecin ayant réalisé la transmission.
     * Ce champ est obligatoire.
     */
    @NotBlank(message = "Le nom du médecin est obligatoire.")
    private String nomMedecin;

    /**
     * Prénom du médecin ayant réalisé la transmission.
     * Ce champ est obligatoire.
     */
    @NotBlank(message = "Le prenom du médecin est obligatoire.")
    String prenomMedecin;

    /**
     * Contenu textuel de la transmission.
     * Ce champ est obligatoire et ne peut pas être vide.
     */
    @NotBlank(message = "La transmission ne peut pas être vide")
    private String transmission;

}
