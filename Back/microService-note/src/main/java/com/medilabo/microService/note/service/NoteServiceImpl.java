package com.medilabo.microService.note.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medilabo.microService.note.model.Note;
import com.medilabo.microService.note.repository.INoteRepository;

@Service
public class NoteServiceImpl implements INoteService {
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private INoteRepository noteRepository;
	
	public List<Note> getAllNotes() {
		logger.info("Tentative de récupération de toutes les transmissions.");
		List<Note> transmissionList = noteRepository.findAll();
		if (transmissionList.isEmpty()) {
			logger.warn("Attention, la liste est vide : {}", transmissionList);
		}
		logger.info("Transmissions récupérée : {}", transmissionList);
		return transmissionList;
	}
	
	public Note getNote(String id) {
		logger.info("Tentative de récupérer une note par son Id.");
	    if (id == null || id.isBlank()) {
	        logger.error("L'id ne peut pas être null ou vide. Id = {}", id);
	        throw new IllegalArgumentException("L'id ne peut pas être null ou vide. Id = " + id);
	    }
	    Note transmission = noteRepository.findById(id)
	    		.orElseThrow(() -> {
	    		    logger.warn("Aucune note trouvée");
	    		    return new NoSuchElementException("Aucune note trouvée");
	    		});
		logger.info("La note récupéré avec l'id {} : {}", id, transmission);
		return transmission;
	}
	
	public Note addNote(Note newNote) {
	    logger.info("Tentative d'ajout d'une nouvelle note.");
	    if (newNote == null) {
	        logger.error("La note à ajouter ne peut pas être null.");
	        throw new IllegalArgumentException("La note à ajouter ne peut pas être null.");
	    }

	    if (newNote.getNomMedecin() == null || newNote.getNomMedecin().isBlank()
	            || newNote.getPrenomMedecin() == null || newNote.getPrenomMedecin().isBlank()
	            || newNote.getNote() == null || newNote.getNote().isBlank()) {
	        logger.error("Le nom et prénom du médecin ainsi que la note écrite sont obligatoires. La note : {}", newNote);
	        throw new IllegalArgumentException("Le nom et prénom du médecin ainsi que la note écrite sont obligatoires.");
	    }

	    Note note = new Note();
	    note.setDateNote(LocalDateTime.now());
	    note.setNomMedecin(newNote.getNomMedecin());
	    note.setPrenomMedecin(newNote.getPrenomMedecin());
	    note.setPatientId(newNote.getPatientId());
	    note.setNote(newNote.getNote());

	    Note noteAdded = noteRepository.save(note);

	    logger.info("La note ajouté : {}", noteAdded);

	    return noteAdded;
	}
	
	public void deleteNote(String id) {
		logger.info("Tentative de suppression de la note par son id.");
		logger.debug("id de la note : {}", id);
		if (id == null || id.isBlank()) {
			logger.error("L'id ne peut pas être null ou vide : {}", id);
			throw new IllegalArgumentException("L'id ne peut pas être null ou vide : "+ id);
		}
		
	    if (!noteRepository.existsById(id)) {
	        logger.warn("Aucune note trouvée avec l'id : {}", id);
	        throw new NoSuchElementException("Aucune note trouvée avec l'id : " + id);
	    }
		
	    noteRepository.deleteById(id);
	}
	
	public Note updateNote(String id, Note updatedNote) {
	    logger.info("Tentative de mise à jour de la note avec l'id : {}", id);
	    logger.debug("note reçue pour mise à jour : {}", updatedNote);
	    
	    if (id == null || id.isBlank()) {
	        logger.error("L'id ne peut pas être null ou vide : {}", id);
	        throw new IllegalArgumentException("L'id ne peut pas être null ou vide : " + id);
	    }

	    if (updatedNote == null) {
	        logger.error("La note mise à jour ne peut pas être null.");
	        throw new IllegalArgumentException("La note mise à jour ne peut pas être null.");
	    }

	    Note existing = noteRepository.findById(id)
	            .orElseThrow(() -> {
	                logger.error("Note non trouvée avec l'id : {}", id);
	                return new NoSuchElementException("Note non trouvée avec l'id : " + id);
	            });
	    
	    logger.debug("La note existante : {}", existing);

	    updatedNote.setDateNote(LocalDateTime.now());

	    Note saved = noteRepository.save(updatedNote);

	    logger.info("Note mise à jour avec succès : {}", saved.getId());

	    return saved;
	}
	
	public List<Note> getAllNotesByPatientId(String patientId) {
	    logger.info("Tentative de récupération des notes du patient avec l'id : {}", patientId);

	    if (patientId == null || patientId.isBlank()) {
	        logger.error("L'id du patient ne peut pas être null ou vide : {}", patientId);
	        throw new IllegalArgumentException("L'id du patient ne peut pas être null ou vide : " + patientId);
	    }

	    List<Note> listNotePatient = noteRepository.findAllByPatientId(patientId);

	    if (listNotePatient == null || listNotePatient.isEmpty()) {
	        logger.warn("Aucune note trouvée pour le patient avec l'id : {}", patientId);
	    } else {
	        logger.info("Notes trouvées pour le patient avec l'id {} : {}", patientId, listNotePatient);
	    }
		return listNotePatient;
	}
	
	public String riskEvaluation(List<Note> transmissions, String patientId) {
	    logger.info("Début de l'évaluation du risque pour le patient avec l'id : {}", patientId);

	    if (patientId == null || patientId.isBlank()) {
	        logger.error("L'id du patient ne peut pas être null ou vide : {}", patientId);
	        throw new IllegalArgumentException("L'id du patient ne peut pas être null ou vide : " + patientId);
	    }

	    List<Note> listTransmission = getAllNotesByPatientId(patientId);

	    if (listTransmission == null || listTransmission.isEmpty()) {
	        logger.warn("Aucune note disponible pour le patient avec l'id : {}", patientId);
	        return "none";
	    }

	    List<String> keywords = Arrays.asList(
	        "hémoglobine", "microalbumine", "réaction", "fumeur", "anormal",
	        "vertiges", "gain de poids", "habitué", "crise", "chute", "hors de vue",
	        "déclencheur", "rechute", "aggravé", "éprouver", "peur", "troublé"
	    );

	    long matchingTransmissionsCount = listTransmission.stream()
	        .filter(transmission -> {
	            String content = transmission.getNote();
	            if (content == null) {
	                return false;
	            }
	            String contentLower = content.toLowerCase();
	            return keywords.stream().anyMatch(keyword -> contentLower.contains(keyword.toLowerCase()));
	        })
	        .count();

	    logger.info("Nombre de transmissions correspondant aux mots-clés pour le patient {} : {}", patientId, matchingTransmissionsCount);

	    if (matchingTransmissionsCount >= 5) {
	        logger.warn("Risque détecté : DANGER pour le patient {}", patientId);
	        return "danger";
	    } else {
	        logger.info("Aucun risque détecté pour le patient {}", patientId);
	        return "none";
	    }
	}

}
