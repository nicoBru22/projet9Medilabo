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

@Service
public class CustomUserDetailService implements UserDetailsService {
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private IUserRepository userRepository;

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
	
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        logger.debug("Attribution du rôle ROLE_{}", role);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return authorities;
    }

}
