package com.medilabo.microService.note.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.microService.note.model.Note;

@Repository
public interface INoteRepository extends MongoRepository<Note, String> {
	List<Note> findAllByPatientId(String patientId);
}
