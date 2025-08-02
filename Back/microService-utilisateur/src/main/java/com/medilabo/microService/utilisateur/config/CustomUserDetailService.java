package com.medilabo.microService.utilisateur.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.medilabo.microService.utilisateur.model.User;
import com.medilabo.microService.utilisateur.repository.IUserRepository;

/**
 * Service de chargement des détails utilisateur pour Spring Security.
 * <p>
 * Cette classe implémente UserDetailsService et permet de récupérer les informations
 * d'un utilisateur (username, mot de passe, rôles) depuis la base de données via IUserRepository.
 * Elle est utilisée par Spring Security lors de l'authentification.
 * </p>
 */
@Service
public class CustomUserDetailService implements UserDetailsService {
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private IUserRepository userRepository;

	/**
	 * Charge un utilisateur à partir de son nom d'utilisateur (username).
	 * <p>
	 * Recherche l'utilisateur dans la base de données, puis retourne un objet UserDetails
	 * contenant les informations nécessaires à Spring Security (username, password, rôles).
	 * </p>
	 * 
	 * @param username nom d'utilisateur (login)
	 * @return UserDetails correspondant à l'utilisateur
	 * @throws UsernameNotFoundException si aucun utilisateur ne correspond au nom fourni
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Utilisateur non trouvé : {}", username);
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
        }

        logger.info("Utilisateur trouvé : {} - Attribution des rôles", username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), getGrantedAuthorities(user.getRole()));
    }
	
	/**
	 * Construit la liste des rôles au format Spring Security à partir du rôle stocké en base.
	 * <p>
	 * Par exemple, si le rôle est "ADMIN", la méthode retourne un GrantedAuthority avec "ROLE_ADMIN".
	 * </p>
	 * 
	 * @param role rôle simple stocké en base (ex: "ADMIN", "USER")
	 * @return liste contenant les rôles au format Spring Security (avec prefixe ROLE_)
	 */
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        logger.debug("Attribution du rôle ROLE_{}", role);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return authorities;
    }

}
