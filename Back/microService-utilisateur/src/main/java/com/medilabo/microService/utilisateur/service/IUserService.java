package com.medilabo.microService.utilisateur.service;

import java.util.List;
import java.util.Map;

import com.medilabo.microService.utilisateur.model.User;

/**
 * Interface du service utilisateur qui définit les opérations disponibles
 * pour gérer les utilisateurs dans l'application.
 * 
 * <p>Ce service permet de récupérer, ajouter, modifier et supprimer des utilisateurs.</p>
 */
public interface IUserService {
	

    /**
     * Récupère la liste de tous les utilisateurs.
     * 
     * @return une liste contenant tous les utilisateurs, une liste vide si aucun utilisateur n'existe
     */
	List<User> getAllUser();
	
    /**Map<String, String> getUsersByIds(Iterable<Long> ids)
     * Récupère un utilisateur par son identifiant unique.
     * 
     * @param id l'identifiant unique de l'utilisateur
     * @return l'utilisateur correspondant à l'identifiant donné
     * @throws IllegalArgumentException si l'id est null ou vide
     * @throws com.medilabo.microService.utilisateur.exception.UserNotFoundException
     *         si aucun utilisateur n'est trouvé avec cet id
     */
	User getUserById(Long id);
	
    /**
     * Récupère un utilisateur par son nom d'utilisateur (username).
     * 
     * @param username le nom d'utilisateur recherché
     * @return l'utilisateur correspondant au nom d'utilisateur donné
     * @throws IllegalArgumentException si le username est null ou vide
     * @throws com.medilabo.microService.utilisateur.exception.UserNotFoundException
     *         si aucun utilisateur n'est trouvé avec ce username
     */
	User getUserByUsername(String username);
	
    /**
     * Ajoute un nouvel utilisateur.
     * 
     * @param user l'objet utilisateur à ajouter, avec ses informations (username, password, nom, prénom, rôle)
     * @return l'utilisateur ajouté avec son identifiant généré
     * @throws IllegalArgumentException si les informations de l'utilisateur sont incomplètes ou invalides
     * @throws com.medilabo.microService.utilisateur.exception.UsernameAlreadyExistsException
     *         si un utilisateur avec le même nom d'utilisateur existe déjà
     */
	User addUser(User user);
	
    /**
     * Supprime un utilisateur par son identifiant.
     * 
     * @param id l'identifiant unique de l'utilisateur à supprimer
     * @throws IllegalArgumentException si l'id est null ou vide
     * @throws com.medilabo.microService.utilisateur.exception.UserNotFoundException
     *         si aucun utilisateur n'est trouvé avec cet id
     */
	void deleteUser(Long id);
	
    /**
     * Met à jour un utilisateur existant identifié par son id.
     * 
     * @param userUpdated l'objet utilisateur contenant les données mises à jour
     * @param id l'identifiant unique de l'utilisateur à modifier
     * @return l'utilisateur mis à jour
     * @throws IllegalArgumentException si l'id est null ou vide, ou si les données utilisateur sont invalides
     * @throws com.medilabo.microService.utilisateur.exception.UserNotFoundException
     *         si aucun utilisateur n'est trouvé avec cet id
     * @throws com.medilabo.microService.utilisateur.exception.UsernameAlreadyExistsException
     *         si le nouveau nom d'utilisateur est déjà utilisé par un autre utilisateur
     */
	User updateUser(User userUpdated, Long id);
	Map<Long, Map<String, String>> getUsersByIds(Iterable<Long> ids);
}
