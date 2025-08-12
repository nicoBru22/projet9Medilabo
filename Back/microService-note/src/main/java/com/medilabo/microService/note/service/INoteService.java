package com.medilabo.microService.note.service;

import java.util.List;

import com.medilabo.microService.note.model.Note;

public interface INoteService {
	List<Note> getAllNotes();
	Note getNote(String id);
	Note addNote(Note newNote, String token);
	public void deleteNote(String id);
	Note updateNote(String id, Note updatedNote);
	List<Note> getAllNotesByPatientId(Long patientId);
}
