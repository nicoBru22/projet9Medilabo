package com.medilabo.microService.alerte.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.medilabo.microService.alerte.model.Note;

@FeignClient(name = "note-service", url = "http://api-note:8081")
public interface INoteClient {
    
	@GetMapping("/note/getNotesPatient")
	List<Note> getAllNotesPatient(@RequestParam Long patientId);
    
}
