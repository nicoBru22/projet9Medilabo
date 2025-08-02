package com.medilabo.gateway;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Composant utilitaire pour la gestion des JSON Web Tokens (JWT).
 * <p>
 * Cette classe fournit des méthodes pour valider un token JWT,
 * ainsi que pour extraire des informations (claims) telles que
 * le nom d'utilisateur (subject) et le rôle associé.
 * </p>
 * <p>
 * Elle utilise la même clé secrète que celle définie dans le microservice utilisateur
 * afin d'assurer la cohérence lors de la validation des tokens générés.
 * </p>
 */
@Component
public class JwtUtil {

    /**
     * Clé secrète injectée directement depuis les variables d'environnement.
     * Le nom de la variable est défini dans le fichier .env et transmis par docker-compose.yml.
     */
    @Value("${JWT_SECRET}")
    private String secretKey;
    
    /**
     * Clé de chiffrement générée à partir de la clé secrète.
     */
    private Key key;

    /**
     * Cette méthode sera appelée après l'injection des dépendances,
     * donc après que secretKey soit injectée par Spring.
     */
    /**
     * Cette méthode sera appelée après l'injection des dépendances,
     * donc après que secretKey soit injectée par Spring.
     */
    @PostConstruct
    public void init() {
        // Assurez-vous que la clé a été trouvée
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET n'a pas été défini comme variable d'environnement.");
        }

        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        System.out.println("Clé JWT chargée avec succès depuis l'environnement Docker !"); // Pour confirmation
    }

    /**
     * Valide la validité d'un token JWT.
     * <p>
     * Vérifie la signature et la date d'expiration.
     * </p>
     * 
     * @param token Le token JWT à valider
     * @return true si le token est valide, false sinon (expiré, modifié, etc.)
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            // token invalide (expiré, modifié, etc.)
            return false;
        }
    }


    /**
     * Extrait le nom d'utilisateur (subject) depuis un token JWT.
     * 
     * @param token Le token JWT
     * @return Le nom d'utilisateur contenu dans le token
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrait le rôle depuis les claims du token JWT.
     * 
     * @param token Le token JWT
     * @return Le rôle de l'utilisateur contenu dans le token
     */
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
    
    /**
     * Retourne la clé secrète utilisée pour la signature des tokens.
     * 
     * @return La clé de signature
     */
    public Key getKey() {
        return key;
    }
}

