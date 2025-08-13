package com.medilabo.microService.note.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Medecin {
	Long id;
	String nomMedecin;
	String prenomMedecin;
}
