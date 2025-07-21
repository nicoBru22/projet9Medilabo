package com.medilabo.microService.utilisateur.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.medilabo.microService.utilisateur.exception.UserNotFoundException;
import com.medilabo.microService.utilisateur.exception.UsernameAlreadyExistsException;
import com.medilabo.microService.utilisateur.model.User;
import com.medilabo.microService.utilisateur.repository.IUserRepository;

@Service
public class UserServiceImpl implements IUserService {
	
	private static final Logger logger = LogManager.getLogger();
	private static final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

	@Autowired
	private IUserRepository userRepository;
	
	public List<User> getAllUser() {
		logger.info("Tentative de récupération de la liste d'utilisateur.");
		List<User> listUtilisateur = userRepository.findAll();
		if (listUtilisateur.isEmpty()) {
			logger.warn("Attention, la liste des utilisateurs est vide.");
		}
		logger.info("Le contenu de la liste utilisateur {}", listUtilisateur);
		return listUtilisateur;
	}
	
	public User getUserById(String id) {
		logger.info("Tentative de récupération de l'utilisateur avec l'id : {}", id);
		if (id == null || id.isBlank()) {
			logger.error("L'id utilisateur ne peut pas être null ou vide.");
			throw new IllegalArgumentException("L'id ne peut pas être vide ou nulle");
		}
		User utilisateur = userRepository.findById(id)
			    .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'id : " + id));

		logger.debug("L'utilisateur récupéré : {}", utilisateur);
		return utilisateur;
	}
	
	public User getUserByUsername(String username) {
		logger.info("Tentative de récupération de l'utilisateur par son username.");
		if(username == null || username.isBlank()) {
			logger.error("Le username ne peut pas être null ou vide. Username : {}", username);
			throw new IllegalArgumentException("\"Le username ne peut pas être null ou vide. Username : "+ username);
		}
		User utilisateurFinded = userRepository.findByUsername(username);
		
	    if (utilisateurFinded == null) {
	        logger.warn("Aucun utilisateur trouvé avec le username : {}", username);
	        throw new UserNotFoundException("Utilisateur non trouvé avec le username : " + username);
	    }
	    
		logger.debug("L utilisateur récupéré : {}", utilisateurFinded);
		return utilisateurFinded;
	}
	
	public User addUser(User utilisateur) {
		logger.info("Tentative d'ajout de l'utilisateur : {}", utilisateur);
		
	    if (utilisateur == null 
	            || utilisateur.getUsername() == null || utilisateur.getUsername().isBlank()
	            || utilisateur.getPassword() == null || utilisateur.getPassword().isBlank()
	            || utilisateur.getNom() == null || utilisateur.getNom().isBlank()
	            || utilisateur.getPrenom() == null || utilisateur.getPrenom().isBlank()
	            || utilisateur.getRole() == null || utilisateur.getRole().isBlank()) {

	            logger.error("Utilisateur invalide : {}", utilisateur);
	            throw new IllegalArgumentException("Les informations de l'utilisateur sont incomplètes : " + utilisateur);
	        }
		
	    if (userRepository.findByUsername(utilisateur.getUsername()) != null) {
	        logger.warn("Un utilisateur avec le username '{}' existe déjà.", utilisateur.getUsername());
	        throw new UsernameAlreadyExistsException("Ce nom d'utilisateur est déjà utilisé : " + utilisateur.getUsername());
	    }
		
		String password = bCryptEncoder.encode(utilisateur.getPassword());
		utilisateur.setPassword(password);
		
		User newUtilisateur = userRepository.save(utilisateur);
		logger.info("L'utilisateur a été ajouté avec succès.");
		return newUtilisateur;
	}
	
	public void deleteUser(String id) {
		logger.info("Tentative de suppression d'un utilisateur par son Id.");
		if (id ==null || id.isBlank()) {
			logger.error("L'Id ne peut être null ou vide : ", id);
			throw new IllegalArgumentException("L'id ne peut être null ou vide :"+ id);
		}
		
	    if (!userRepository.existsById(id)) {
	        logger.warn("Aucun utilisateur trouvé avec l'id : {}", id);
	        throw new UserNotFoundException("Aucun utilisateur trouvé avec l'id : " + id);
	    }
	    
		userRepository.deleteById(id);
	}
	
	public User updateUser(User userUpdated, String id) {
	    logger.info("Tentative de mise à jour de l'utilisateur avec l'id : {}", id);
	    
	    if (id == null || id.isBlank() || userUpdated == null) {
	        logger.error("ID ou données utilisateur invalides.");
	        throw new IllegalArgumentException("ID ou données utilisateur invalides.");
	    }
	    User userToUpdate = userRepository.findById(id)
	            .orElseThrow(() -> {
	                logger.warn("Aucun utilisateur trouvé avec l'id : {}", id);
	                return new UserNotFoundException("Utilisateur non trouvé avec l'id : " + id);
	            });
	    
	    if (!userToUpdate.getUsername().equals(userUpdated.getUsername())
	            && userRepository.findByUsername(userUpdated.getUsername()) != null) {
	            logger.warn("Le nom d'utilisateur '{}' est déjà pris.", userUpdated.getUsername());
	            throw new UsernameAlreadyExistsException("Ce nom d'utilisateur est déjà utilisé.");
	        }
	    
	    if (userUpdated.getPassword() != null && !userUpdated.getPassword().isBlank()) {
	        String passwordEncoded = bCryptEncoder.encode(userUpdated.getPassword());
	        userToUpdate.setPassword(passwordEncoded);
	    }
	    
		userToUpdate.setNom(userUpdated.getNom());
		userToUpdate.setPrenom(userUpdated.getPrenom());
		userToUpdate.setUsername(userUpdated.getUsername());
		userToUpdate.setPassword(userUpdated.getPassword());
		userToUpdate.setRole(userUpdated.getRole());
		
	    User updatedUser = userRepository.save(userToUpdate);
	    logger.info("Mise à jour réussie : {}", updatedUser);

	    return updatedUser;
		
	}
		
}
