package com.medilabo.microService.utilisateur.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.microService.utilisateur.model.User;


@Repository
public interface IUserRepository extends MongoRepository<User, String> {
	User findByUsername(String username);
}