package com.medilabo.microService.utilisateur.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
		logger.info("Le contenu de la liste utilisateur {}", listUtilisateur);
		return listUtilisateur;
	}
	
	public User getUserById(String id) {
		User utilisateur = userRepository.findById(id).get();
		return utilisateur;
	}
	
	public User getUserByUsername(String username) {
		User utilisateurFinded = userRepository.findByUsername(username);
		return utilisateurFinded;
	}
	
	public User addUser(User utilisateur) {
		logger.info("Tentative d'ajout de l'utilisateur : {}", utilisateur);
		
		
		String password = bCryptEncoder.encode(utilisateur.getPassword());
		utilisateur.setPassword(password);
		
		User newUtilisateur = userRepository.save(utilisateur);
		logger.info("L'utilisateur a été ajouté avec succès.");
		return newUtilisateur;
	}
	
	public void deleteUser(String id) {
		userRepository.deleteById(id);
	}
	
	public User updateUser(User userUpdated, String id) {

		User userToUpdate = userRepository.findById(id).get();
		userToUpdate.setNom(userUpdated.getNom());
		userToUpdate.setPrenom(userUpdated.getPrenom());
		userToUpdate.setUsername(userUpdated.getUsername());
		userToUpdate.setPassword(userUpdated.getPassword());
		userToUpdate.setRole(userUpdated.getRole());
		
		userRepository.save(userToUpdate);
		
		return userToUpdate;
		
	}
		
}
