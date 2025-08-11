package com.medilabo.microService.note.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medilabo.microService.note.model.Note;
import com.medilabo.microService.note.service.INoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/note")
public class NoteController {
	private static final Logger logger = LogManager.getLogger(NoteController.class);

	@Autowired
	private INoteService noteService;

	@PostMapping("/add")
	public ResponseEntity<Note> addNote(@Valid @RequestBody Note newNote) {
	    logger.info("Requête reçue pour ajouter une note : {}", newNote);
	    Note noteAdded = noteService.addNote(newNote);
	    logger.info("Transmission ajoutée avec succès : {}", noteAdded);
	    return ResponseEntity.status(HttpStatus.CREATED).body(noteAdded);
	}

	@GetMapping("/getNotesPatient")
	public ResponseEntity<List<Note>> getAllNotesPatient(@RequestParam String patientId) {
	    logger.info("Requête reçue pour récupérer les notes du patient avec l'ID : {}", patientId);
	    List<Note> noteList = noteService.getAllNotesByPatientId(patientId);
	    if (noteList.isEmpty()) {
	        logger.warn("Aucune note trouvée pour le patient avec l'ID : {}", patientId);
	    } else {
	        logger.info("{} notes trouvées pour le patient {}", noteList.size(), patientId);
	    }
	    return ResponseEntity.status(HttpStatus.OK).body(noteList);
	}

}
