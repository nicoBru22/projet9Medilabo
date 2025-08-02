package com.medilabo.microService.utilisateur.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de la sécurité pour le microservice utilisateur.
 * <p>
 * Cette classe configure les aspects liés à la sécurité HTTP, 
 * notamment la gestion des filtres JWT, les règles d'autorisation, 
 * l'encodage des mots de passe et le gestionnaire d'authentification.
 * </p>
 */
@Configuration
public class SecurityConfig {
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private CustomUserDetailService customUserDetailService;
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;


    /**
     * Définit la chaîne de filtres de sécurité HTTP.
     * <p>
     * - Désactive la protection CSRF (Cross-Site Request Forgery).<br>
     * - Ajoute un filtre personnalisé pour l'authentification JWT avant le filtre standard UsernamePasswordAuthenticationFilter.<br>
     * - Configure les règles d'accès aux endpoints (autorisation) :<br>
     *   - Autorise l'accès libre aux endpoints de login, de santé, et à la documentation Swagger.<br>
     *   - Nécessite une authentification pour toutes les autres requêtes.<br>
     * - Configure la gestion des sessions pour limiter à une session simultanée par utilisateur.
     * </p>
     * 
     * @param http l'objet HttpSecurity à configurer
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/utilisateur/login").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/utilisateur/health").permitAll()
                        .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                                .maximumSessions(1)
                );

		logger.info("Configuration de la securité appliquée avec succès.");

		return http.build();
		
	}
	
    /**
     * Bean pour encoder les mots de passe avec l'algorithme BCrypt.
     * 
     * @return un encodeur de mot de passe BCryptPasswordEncoder
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        logger.debug("Initialisation du BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean pour configurer le gestionnaire d'authentification avec le service utilisateur et l'encodeur de mot de passe.
     * 
     * @param http l'objet HttpSecurity partagé
     * @param bCryptPasswordEncoder l'encodeur de mot de passe BCrypt
     * @return un gestionnaire d'authentification configuré
     * @throws Exception en cas d'erreur lors de la configuration
     */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        logger.info("Configuration du gestionnaire d'authentification");

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailService).passwordEncoder(bCryptPasswordEncoder);

        logger.info("Gestionnaire d'authentification configuré avec succès");

        return authenticationManagerBuilder.build();
    }
}
