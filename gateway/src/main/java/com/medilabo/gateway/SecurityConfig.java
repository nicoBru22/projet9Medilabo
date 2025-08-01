package com.medilabo.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    public SecurityConfig(JwtAuthenticationWebFilter jwtAuthenticationWebFilter) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // 1. Autoriser l'accès à l'interface Swagger UI SERVIE PAR LA GATEWAY
                // Ceci inclut la page HTML, les CSS, les JS, etc.
                .pathMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",      // Ressources statiques de Swagger UI
                    "/v3/api-docs/**",     // Les définitions OpenAPI (y compris celle de la gateway elle-même)
                    "/webjars/**"          // Dépendances web de Swagger UI
                ).permitAll()

                // 2. Autoriser l'accès aux chemins des api-docs des microservices
                // C'est redondant avec "/v3/api-docs/**" mais plus explicite.
                // La gateway va intercepter ces appels et les router vers les services.
                .pathMatchers(
                    "/patient/v3/api-docs",
                    "/utilisateur/v3/api-docs"
                ).permitAll()

                // 3. Vos autres chemins publics
                .pathMatchers("/utilisateur/login").permitAll()
                .pathMatchers("/patient/health").permitAll()
                .pathMatchers("/utilisateur/health").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                

                // 4. Sécuriser tout le reste
                .anyExchange().authenticated()
            )
            // Assurez-vous que votre filtre est ajouté correctement
            .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        System.out.println("Configuration de sécurité de la Gateway initialisée.");

        return http.build();
    }



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
