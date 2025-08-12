package com.medilabo.microService.note.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.junit.jupiter.MockitoExtension; // <-- Ajout de MockitoExtension

import com.medilabo.microService.note.config.JwtUtil;
import com.medilabo.microService.note.model.Note;
import com.medilabo.microService.note.repository.INoteRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TransmissionServiceTest {
	
	@Mock
	private JwtUtil jwtUtil;
	
	@Mock
	private INoteRepository noteRepository;

	@InjectMocks
	private NoteServiceImpl noteService;
	
	private static Note noteTest1;
	private static Note noteTest2;
	private static List<Note> listNoteTest;
	
	@BeforeAll
	public static void setup() {
		noteTest1 = new Note(
				"1",
				1L,
				1L,
				LocalDateTime.now(),
				"une note sans probleme");
		noteTest2 = new Note(
				"2",
				1L,
				1L,
				LocalDateTime.now(),
				"une note avec 5 problemes : hémoglobine, microalbumine, réaction, fumeur, anormal");
		listNoteTest = List.of(noteTest1, noteTest2);
	}
	
	@Test
	public void getAllNotesServiceTest() {
		when(noteRepository.findAll()).thenReturn(listNoteTest);
		
		List<Note> result = noteService.getAllNotes();

	    assertThat(2).isEqualTo(result.size());
	    
	    assertThat(result).contains(noteTest1, noteTest2);
		
		verify(noteRepository, times(1)).findAll();
	}
	
	@Test
	public void getNoteServiceTest() {
		when(noteRepository.findById("1")).thenReturn(Optional.of(noteTest1));
		
		Note result = noteService.getNote("1");
		
		assertThat(noteTest1).isEqualTo(result);
		
		verify(noteRepository, times(1)).findById("1");
	}

	@Test
	public void addNoteServiceTest() {
	    when(jwtUtil.extractUserId("mock-token")).thenReturn(99L);
	    when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
	        Note savedNote = invocation.getArgument(0);
	        savedNote.setId("1");
	        return savedNote;
	    });
	    Note newNote = new Note();
	    newNote.setPatientId(1L);
	    newNote.setNote("une note sans probleme");

	    Note result = noteService.addNote(newNote, "mock-token");

	    assertThat(result).isNotNull();
	    assertThat(result.getId()).isEqualTo("1");
	    assertThat(result.getMedecinId()).isEqualTo(99L);
	    assertThat(result.getPatientId()).isEqualTo(1L);
	    assertThat(result.getNote()).isEqualTo("une note sans probleme");
	    
	    verify(jwtUtil, times(1)).extractUserId("mock-token");
	    verify(noteRepository, times(1)).save(any(Note.class));
	}

	
	@Test
	public void deleteNoteServiceTest() {
        when(noteRepository.existsById("1")).thenReturn(true);
        
        doNothing().when(noteRepository).deleteById("1");

        noteService.deleteNote("1");

        verify(noteRepository, times(1)).existsById("1");
        verify(noteRepository, times(1)).deleteById("1");
	}
	
	@Test
	public void updateNoteServiceTest() {
        when(noteRepository.findById("1")).thenReturn(Optional.of(noteTest1));
        
        when(noteRepository.save(any(Note.class))).thenReturn(noteTest1);
        
        Note result = noteService.updateNote("1", noteTest1);
        
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(noteTest1);
        
        verify(noteRepository, times(1)).findById("1");
        verify(noteRepository, times(1)).save(any(Note.class));
	}
	
	@Test
	public void getAllNotesByPatientIdServiceTest() {
		when(noteRepository.findAllByPatientId("1")).thenReturn(listNoteTest);
		
		List<Note> result = noteService.getAllNotesByPatientId("1");
		
		assertThat(result).isNotNull();
		assertThat(result).contains(noteTest1, noteTest2);
		
        verify(noteRepository, times(1)).findAllByPatientId("1");
		
	}
	
	@Test
	public void riskEvaluationNoneServiceTest() {
		List<Note> listNone = List.of(noteTest1);
		
		when(noteRepository.findAllByPatientId("1")).thenReturn(listNone);
		
		String result = noteService.riskEvaluation(listNone, "1");
		
		assertThat(result).isEqualTo("none");
		
		verify(noteRepository, times(1)).findAllByPatientId("1");
	}
	
	@Test
	public void riskEvaluationDangerServiceTest() {
	    List<Note> listDanger = List.of(noteTest2);
	    
	    when(noteRepository.findAllByPatientId("2")).thenReturn(listDanger);

	    String result = noteService.riskEvaluation(listDanger, "2");

	    assertThat(result).isEqualTo("none");
	}


	
	
}
