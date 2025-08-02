package com.medilabo.microService.utilisateur.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Classe utilitaire pour la gestion des JSON Web Tokens (JWT).
 * <p>
 * Cette classe permet de générer, valider et extraire des informations des tokens JWT utilisés
 * pour l'authentification et l'autorisation des utilisateurs.
 * </p>
 * 
 * <p>
 * Le token contient des informations personnalisées (claims) telles que le nom d'utilisateur,
 * le rôle, le prénom et le nom, avec une durée de validité de 10 heures.
 * </p>
 * 
 * <p>
 * Le secret utilisé pour signer les tokens est une clé symétrique stockée ici en dur (à considérer pour une amélioration de sécurité).
 * </p>
 */
@Component
public class JwtUtil {


    /**
     * Clé secrète utilisée pour la signature du JWT.
     * <p>
     * Attention : pour la production, il est conseillé de stocker cette clé en sécurité (ex: variables d'environnement).
     * </p>
     */
	private static final String SECRET_KEY = "fF3uM8XbZ9LpQwErTyUiOpAsDfGhJkL1";

    /**
     * Durée de validité du token (en millisecondes).
     * Ici, 10 heures.
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 heures

    /**
     * Clé symétrique utilisée pour la signature des JWT.
     */
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Génère un token JWT avec les informations utilisateur.
     * 
     * @param username le nom d'utilisateur (subject)
     * @param role le rôle de l'utilisateur (claim personnalisé)
     * @param prenom le prénom de l'utilisateur (claim personnalisé)
     * @param nom le nom de famille de l'utilisateur (claim personnalisé)
     * @return un token JWT signé et compacté sous forme de chaîne
     */
    public String generateToken(String username, String role, String prenom, String nom) {
        return Jwts.builder()
                .setSubject(username)
                .claim("username", username)
                .claim("role", role)
                .claim("prenom", prenom)
                .claim("nom", nom)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Retourne la clé utilisée pour la signature du JWT.
     * 
     * @return la clé secrète
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Vérifie la validité d'un token JWT.
     * <p>
     * Un token est valide s'il est correctement signé et non expiré.
     * En cas d'exception (token expiré, modifié, ou invalide), retourne false.
     * </p>
     * 
     * @param token le token JWT à valider
     * @return true si le token est valide, false sinon
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
     * @param token le token JWT
     * @return le nom d'utilisateur contenu dans le token
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
     * Extrait le rôle depuis un token JWT.
     * 
     * @param token le token JWT
     * @return le rôle de l'utilisateur contenu dans le token
     */
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

}

