package com.medilabo.microService.utilisateur.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.microService.utilisateur.config.JwtUtil;
import com.medilabo.microService.utilisateur.model.User;
import com.medilabo.microService.utilisateur.model.UserDto;
import com.medilabo.microService.utilisateur.service.IUserService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;



@RestController
@RequestMapping("/utilisateur")
public class UserController {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	public JwtUtil jwtUtil;

	
	@GetMapping("/list")
	public ResponseEntity<List<User>> getListUsers() {
		logger.info("Requête reçu sur le controller getListUsers()");
		List<User> userList = userService.getAllUser();
		logger.info("Récupération de la liste avec succès.");
		return ResponseEntity.ok(userList);
	}
	
	@PostMapping("/add")
	public ResponseEntity<User> addUser(@RequestBody @Valid User newUser) {
		logger.info("Requête reçu sur le controller addUser()");
		User userAdded = userService.addUser(newUser);		
		return ResponseEntity.status(HttpStatus.CREATED).body(userAdded);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable String id) {
		logger.info("Requête reçu sur le controller deleteUser()");
		userService.deleteUser(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody @Valid User userToUpdate, BindingResult result) {
		logger.info("Requête reçu sur le controller updateUser()");
		if(result.hasErrors()) {
			logger.error("Une erreur s'est produite lors de la mise à jour de l utilisateur {}", userToUpdate);
			ResponseEntity.internalServerError();
		}
		User userUpdated = userService.updateUser(userToUpdate, id);		
		return ResponseEntity.status(HttpStatus.CREATED).body(userUpdated);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDto userDto, HttpServletRequest httpRequest) {
		logger.info("Requête reçu sur le controller login()");
	    try {
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
	        );
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        // Récupérer l'utilisateur complet depuis le service
	        User user = userService.getUserByUsername(userDto.getUsername());

	        // Générer le token avec toutes les infos nécessaires
	        String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getPrenom(), user.getNom());
	        
	        logger.info("Utilisateur authentifié: " + user.getUsername());
	        logger.info("Token JWT généré: " + jwt);


	        return ResponseEntity.ok(Map.of(
	            "status", "success",
	            "token", jwt
	        ));

	    } catch (AuthenticationException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Échec d'authentification");
	    }
	}

	
}
