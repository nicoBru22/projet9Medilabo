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

/**
 * Implémentation du service utilisateur pour la gestion des opérations
 * CRUD sur les utilisateurs.
 * <p>
 * Cette classe fournit les méthodes pour récupérer, ajouter, modifier,
 * et supprimer des utilisateurs, avec gestion des exceptions spécifiques.
 * Le mot de passe est automatiquement encodé lors de la création ou
 * mise à jour d'un utilisateur.
 * </p>
 */
@Service
public class UserServiceImpl implements IUserService {
	
	private static final Logger logger = LogManager.getLogger();
	
    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptEncoder;

    // Injection via le constructeur
    public UserServiceImpl(IUserRepository userRepository, BCryptPasswordEncoder bCryptEncoder) {
        this.userRepository = userRepository;
        this.bCryptEncoder = bCryptEncoder;
    }
	
	/**
	 * Récupère la liste de tous les utilisateurs.
	 * 
	 * @return une liste contenant tous les utilisateurs
	 */
	public List<User> getAllUser() {
		logger.info("Tentative de récupération de la liste d'utilisateur.");
		List<User> listUtilisateur = userRepository.findAll();
		if (listUtilisateur.isEmpty()) {
			logger.warn("Attention, la liste des utilisateurs est vide.");
		}
		logger.info("Le contenu de la liste utilisateur {}", listUtilisateur);
		return listUtilisateur;
	}
	
	/**
	 * Récupère un utilisateur par son identifiant unique.
	 * 
	 * @param id l'identifiant de l'utilisateur
	 * @return l'utilisateur correspondant à l'id
	 * @throws IllegalArgumentException si l'id est null ou vide
	 * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet id
	 */	
	public User getUserById(Long id) {
		logger.info("Tentative de récupération de l'utilisateur avec l'id : {}", id);
		if (id == null) {
			logger.error("L'id utilisateur ne peut pas être null");
			throw new IllegalArgumentException("L'id ne peut pas être vide ou nulle");
		}
		User utilisateur = userRepository.findById(id)
			    .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'id : " + id));

		logger.debug("L'utilisateur récupéré : {}", utilisateur);
		return utilisateur;
	}
	
	/**
	 * Récupère un utilisateur par son nom d'utilisateur (username).
	 * 
	 * @param username le nom d'utilisateur
	 * @return l'utilisateur correspondant au username
	 * @throws IllegalArgumentException si le username est null ou vide
	 * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec ce username
	 */
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
	
	/**
	 * Ajoute un nouvel utilisateur en encodant son mot de passe.
	 * 
	 * @param utilisateur l'objet utilisateur à ajouter
	 * @return l'utilisateur ajouté avec son mot de passe encodé
	 * @throws IllegalArgumentException si les informations de l'utilisateur sont incomplètes ou nulles
	 * @throws UsernameAlreadyExistsException si un utilisateur avec le même username existe déjà
	 */
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
	
	/**
	 * Supprime un utilisateur par son identifiant.
	 * 
	 * @param id l'identifiant de l'utilisateur à supprimer
	 * @throws IllegalArgumentException si l'id est null ou vide
	 * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet id
	 */
	public void deleteUser(Long id) {
		logger.info("Tentative de suppression d'un utilisateur par son Id.");
		if (id ==null) {
			logger.error("L'Id ne peut être null ou vide : ", id);
			throw new IllegalArgumentException("L'id ne peut être null ou vide :"+ id);
		}
		
	    if (!userRepository.existsById(id)) {
	        logger.warn("Aucun utilisateur trouvé avec l'id : {}", id);
	        throw new UserNotFoundException("Aucun utilisateur trouvé avec l'id : " + id);
	    }
	    
		userRepository.deleteById(id);
	}
	
	/**
	 * Met à jour un utilisateur existant.
	 * <p>
	 * Si le mot de passe est modifié, il est encodé avant sauvegarde.
	 * </p>
	 * 
	 * @param userUpdated l'objet utilisateur contenant les nouvelles informations
	 * @param id l'identifiant de l'utilisateur à mettre à jour
	 * @return l'utilisateur mis à jour
	 * @throws IllegalArgumentException si l'id est null ou vide, ou si l'objet utilisateur est null
	 * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet id
	 * @throws UsernameAlreadyExistsException si le nouveau nom d'utilisateur est déjà pris par un autre utilisateur
	 */
	public User updateUser(User userUpdated, Long id) {
	    logger.info("Tentative de mise à jour de l'utilisateur avec l'id : {}", id);
	    logger.info("le userupdated : {}", userUpdated);
	    if (id == null || userUpdated == null) {
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
	    
		userToUpdate.setNom(userUpdated.getNom());
		userToUpdate.setPrenom(userUpdated.getPrenom());
		userToUpdate.setUsername(userUpdated.getUsername());
		userToUpdate.setRole(userUpdated.getRole());
	    
	    if (userUpdated.getPassword() != null && !userUpdated.getPassword().isBlank()) {
	    	logger.info("le mot de passe n'est pas null et n'est pas vide.");
	        String passwordEncoded = bCryptEncoder.encode(userUpdated.getPassword());
	        userToUpdate.setPassword(passwordEncoded);
	    }
		
	    User updatedUser = userRepository.save(userToUpdate);
	    logger.info("Mise à jour réussie : {}", updatedUser);

	    return updatedUser;
		
	}
		
}
