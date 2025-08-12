package com.medilabo.microService.utilisateur.controller;

import java.util.HashMap;
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


/**
 * Contrôleur REST pour la gestion des utilisateurs.
 * <p>
 * Cette classe expose les points d'entrée HTTP pour les opérations CRUD sur les utilisateurs,
 * ainsi que l'authentification via un endpoint de login qui génère un token JWT.
 * </p>
 */
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
	
    /**
     * Récupère la liste complète des utilisateurs.
     * 
     * @return ResponseEntity contenant la liste des utilisateurs et le code HTTP 200 OK.
     */
	@GetMapping("/list")
	public ResponseEntity<List<User>> getListUsers() {
		logger.info("Requête reçu sur le controller getListUsers()");
		List<User> userList = userService.getAllUser();
		logger.info("Récupération de la liste avec succès.");
		return ResponseEntity.ok(userList);
	}
	
    /**
     * Ajoute un nouvel utilisateur.
     * 
     * @param newUser l'utilisateur à ajouter, validé selon les contraintes définies
     * @return ResponseEntity contenant l'utilisateur créé et le code HTTP 201 CREATED
     */
	@PostMapping("/add")
	public ResponseEntity<?> addUser(@RequestBody @Valid User newUser, BindingResult result) {
		logger.info("Requête reçu sur le controller addUser()");
		
	    if (result.hasErrors()) {
	    	logger.info("Une erreur s'est produite lors de l'ajout du nouvel utilisateur.");
	        Map<String, String> errors = new HashMap<>();
	        result.getFieldErrors().forEach(error -> 
	            errors.put(error.getField(), error.getDefaultMessage())
	        );
	        return ResponseEntity.badRequest().body(errors);
	    }
		User userAdded = userService.addUser(newUser);	
		return ResponseEntity.status(HttpStatus.CREATED).body(userAdded);
	}
	
    /**
     * Supprime un utilisateur par son identifiant.
     * 
     * @param id l'identifiant de l'utilisateur à supprimer
     * @return ResponseEntity avec le code HTTP 204 NO CONTENT en cas de succès
     */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		logger.info("Requête reçu sur le controller deleteUser()");
		userService.deleteUser(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
    /**
     * Met à jour un utilisateur existant.
     * 
     * @param id l'identifiant de l'utilisateur à mettre à jour
     * @param userToUpdate les données mises à jour de l'utilisateur, validées
     * @param result résultat de la validation des données
     * @return ResponseEntity contenant l'utilisateur mis à jour et le code HTTP 201 CREATED
     *         ou une erreur 500 en cas de problème de validation
     */
	@PutMapping("/update/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid User userToUpdate, BindingResult result) {
		logger.info("Requête reçu sur le controller updateUser()");
		
		if(result.hasErrors()) {
			logger.error("Une erreur s'est produite lors de la mise à jour de l utilisateur {}", userToUpdate);
			Map<String, String> errors = new HashMap<>();
	        result.getFieldErrors().forEach(error -> 
            	errors.put(error.getField(), error.getDefaultMessage()));
    			ResponseEntity.badRequest().body(errors);
		}
		User userUpdated = userService.updateUser(userToUpdate, id);		
		return ResponseEntity.status(HttpStatus.CREATED).body(userUpdated);
	}
	
    /**
     * Authentifie un utilisateur et génère un token JWT.
     * 
     * @param userDto les informations d'identification (username et password)
     * @param httpRequest la requête HTTP (non utilisée ici mais injectable)
     * @return ResponseEntity contenant le token JWT en cas de succès avec statut 200 OK,
     *         ou un message d'erreur avec statut 401 UNAUTHORIZED en cas d'échec d'authentification
     */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDto userDto, HttpServletRequest httpRequest) {
		logger.info("Requête reçu sur le controller login()");
	    try {
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
	        );
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        
	        User user = userService.getUserByUsername(userDto.getUsername());
	        String jwt = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getPrenom(), user.getNom());
	        
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
