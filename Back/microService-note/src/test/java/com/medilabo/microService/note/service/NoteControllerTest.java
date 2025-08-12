package com.medilabo.microService.note.service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.microService.note.controller.NoteController;
import com.medilabo.microService.note.model.Note;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NoteController.class)
@ActiveProfiles("test")
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private INoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    private static Note noteTest1;
    private static Note noteTest2;
    private static List<Note> listNoteTest;

    @BeforeAll
    public static void setup() {
	    ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        noteTest1 = new Note(
                "1",
                1L,
                1L,
                LocalDateTime.now(),
                "une note sans probleme"
        );
        noteTest2 = new Note(
                "2",
                1L,
                1L,
                LocalDateTime.now(),
                "une note avec 5 problemes : hémoglobine, microalbumine, réaction, fumeur, anormal"
        );
        listNoteTest = List.of(noteTest1, noteTest2);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testAddNote() throws Exception {
    	when(noteService.addNote(
    	        org.mockito.ArgumentMatchers.any(Note.class),
    	        org.mockito.ArgumentMatchers.any(String.class)
    	)).thenReturn(noteTest1);
	    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        mockMvc.perform(post("/note/add")
        		.header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteTest1)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.patientId").value("1"))
            .andExpect(jsonPath("$.note").value("une note sans probleme"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllNotesPatient() throws Exception {
        when(noteService.getAllNotesByPatientId("1")).thenReturn(listNoteTest);

        mockMvc.perform(get("/note/getNotesPatient")
                .param("patientId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[1].id").value("2"));
    }
}
