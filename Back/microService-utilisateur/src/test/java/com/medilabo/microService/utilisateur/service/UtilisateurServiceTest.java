package com.medilabo.microService.utilisateur.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.medilabo.microService.utilisateur.model.User;
import com.medilabo.microService.utilisateur.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceTest {

	// @InjectMocks injecte les mocks dans l'instance du service
	@InjectMocks
	private UserServiceImpl userService; 

	// @Mock crée des instances mockées des dépendances
	@Mock
	private IUserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder bCryptEncoder;
	
	@Test
	void addUserTest() {
        User userTest1 = new User(null, "nbrunet", "Brunet", "Nicolas", "pwd123", "USER");
        User userSaved = new User(1L, "nbrunet", "Brunet", "Nicolas", "pwd123", "USER");
        
        when(userRepository.save(userTest1)).thenReturn(userSaved);
   
        
        User userAdded = userService.addUser(userTest1);
        
        verify(userRepository, times(1)).save(userTest1);
        
        assertThat(userAdded).isEqualTo(userSaved);
		
	}
	
    @Test
    void getAllUserTest() {
        User userTest1 = new User(1L, "nbrunet", "Brunet", "Nicolas", "pwd123", "USER");
        User userTest2 = new User(2L, "spiet", "Piet", "Sarah", "pwd456", "ADMIN");
        List<User> listUserMocked = List.of(userTest1, userTest2);

        when(userRepository.findAll()).thenReturn(listUserMocked);

        List<User> listUser = userService.getAllUser();

        verify(userRepository, times(1)).findAll();
        assertThat(2).isEqualTo(listUser.size());
        assertThat(listUserMocked).isEqualTo(listUser);
    }
	
	@Test
	void getUserByIdTest() {
        User userTest1 = new User(1L, "nbrunet", "Brunet", "Nicolas", "pwd123", "USER");

        when(userRepository.findById(userTest1.getId())).thenReturn(Optional.of(userTest1));
        
        User userGetted = userService.getUserById(userTest1.getId());
        
        verify(userRepository, times (1)).findById(userTest1.getId());
        assertThat(userGetted).isEqualTo(userTest1);
	}
	
	@Test
	void getUserByUsernameTest() {
        User userTest1 = new User(1L, "nbrunet", "Brunet", "Nicolas", "pwd123", "USER");

        when(userRepository.findByUsername(userTest1.getUsername())).thenReturn(userTest1);
        
        User userGetted = userService.getUserByUsername(userTest1.getUsername());
        
        verify(userRepository, times (1)).findByUsername(userTest1.getUsername());
        assertThat(userGetted).isEqualTo(userTest1);
	}
	
	@Test
	void deleteUserTest() {
	    Long userId = 1L;
	    
	    when(userRepository.existsById(userId)).thenReturn(true);

	    userService.deleteUser(userId);

	    verify(userRepository, times(1)).existsById(userId);
	    verify(userRepository, times(1)).deleteById(userId);
	}
	
	@Test
	void updateUserTest() {
		    Long userId = 1L;

		    User existingUser = new User(
		        1L, "oldUsername", "Dupont", "Jean", "oldPwd", "USER"
		    );

		    User userUpdated = new User(
		        null, "newUsername", "Durand", "Luc", "newPwd", "ADMIN"
		    );

		    User updatedUserSaved = new User(
		        1L, "newUsername", "Durand", "Luc", "encodedPwd", "ADMIN"
		    );

		    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		    when(userRepository.findByUsername("newUsername")).thenReturn(null);
		    when(bCryptEncoder.encode("newPwd")).thenReturn("encodedPwd");
		    when(userRepository.save(any(User.class))).thenReturn(updatedUserSaved);

		    User result = userService.updateUser(userUpdated, userId);

		    verify(userRepository).findById(userId);
		    verify(userRepository).findByUsername("newUsername");
		    verify(userRepository).save(any(User.class));
		    verify(bCryptEncoder).encode("newPwd");

		    assertThat(result.getUsername()).isEqualTo("newUsername");
		    assertThat(result.getNom()).isEqualTo("Durand");
		    assertThat(result.getPrenom()).isEqualTo("Luc");
		    assertThat(result.getPassword()).isEqualTo("encodedPwd");
		    assertThat(result.getRole()).isEqualTo("ADMIN");

	}
	
	
	
	
	
	
	
	
}
