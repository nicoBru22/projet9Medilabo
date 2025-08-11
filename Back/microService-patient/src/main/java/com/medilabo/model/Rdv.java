package com.medilabo.model;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rdv {
	
    @Id
    private String id;
    
    private String patientId;
    
    private String medecinId;

    @NotNull(message ="le nom du médecin ne peut pas être null.")
    @NotBlank(message = "Le nom du médecin ne peut pas être vide.")
    private String nomMedecin;
    
    private String jourRdv;
    
    private String heureRdv;

}
