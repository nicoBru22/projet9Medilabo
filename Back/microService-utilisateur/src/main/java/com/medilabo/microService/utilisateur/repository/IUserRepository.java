package com.medilabo.microService.utilisateur.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.microService.utilisateur.model.User;

/**
 * Interface de repository MongoDB pour la gestion des utilisateurs.
 * <p>
 * Étend {@link MongoRepository} pour fournir les opérations CRUD sur les documents {@link User}
 * stockés dans la base MongoDB.
 * </p>
 * 
 * <p>
 * Cette interface permet également de rechercher un utilisateur par son nom d'utilisateur (username).
 * </p>
 */
@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
	
    /**
     * Recherche un utilisateur en fonction de son nom d'utilisateur.
     * 
     * @param username le nom d'utilisateur à rechercher
     * @return l'utilisateur correspondant au nom d'utilisateur, ou {@code null} si aucun utilisateur n'a été trouvé
     */
	User findByUsername(String username);
}