package com.medilabo.microService.utilisateur.service;

import java.util.List;

import com.medilabo.microService.utilisateur.model.User;

public interface IUserService {
	List<User> getAllUser();
	User getUserById(String id);
	User getUserByUsername(String username);
	User addUser(User user);
	void deleteUser(String id);
	User updateUser(User userUpdated, String id);
}
