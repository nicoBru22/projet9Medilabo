package com.medilabo.gateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration de la sécurité pour la gateway API.
 * <p>
 * Cette classe configure les règles de sécurité WebFlux,
 * y compris la gestion des accès publics, la sécurisation des routes,
 * l'ajout du filtre d'authentification JWT, et la configuration CORS.
 * </p>
 * Elle permet notamment :
 * <ul>
 *   <li>L'accès public aux ressources Swagger UI pour la documentation.</li>
 *   <li>L'accès public aux endpoints de login et de santé des microservices.</li>
 *   <li>La sécurisation par défaut de toutes les autres requêtes.</li>
 *   <li>La configuration des règles CORS autorisant les requêtes depuis localhost:3000.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    
    @Autowired
    private JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
    
    private Logger logger = LogManager.getLogger();
    

    /**
     * Configuration de la chaîne de filtres de sécurité WebFlux.
     * 
     * @param http l'objet ServerHttpSecurity pour configurer la sécurité HTTP
     * @return la chaîne de filtres de sécurité configurée
     */
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                .pathMatchers(
                    "/patient/v3/api-docs",
                    "/utilisateur/v3/api-docs"
                ).permitAll()
                .pathMatchers("/utilisateur/login").permitAll()
                .pathMatchers("/patient/health").permitAll()
                .pathMatchers("/utilisateur/health").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        logger.info("Configuration de sécurité de la Gateway initialisée.");

        return http.build();
    }

    /**
     * Configuration des règles CORS autorisant les requêtes cross-origin.
     * <p>
     * Ici, seules les requêtes venant de "http://localhost:3000" sont autorisées,
     * avec les méthodes HTTP GET, POST, PUT, DELETE, OPTIONS,
     * et certains headers spécifiques (Authorization, Cache-Control, Content-Type).
     * </p>
     * 
     * @return la source de configuration CORS à appliquer
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
