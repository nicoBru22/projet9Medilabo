package com.medilabo.gateway;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // Mets ici la même clé secrète que dans l'API utilisateur
	private static final String SECRET_KEY = "fF3uM8XbZ9LpQwErTyUiOpAsDfGhJkL1";


    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Méthode pour valider le token (vérifie signature et expiration)
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

    // Extraire username (ou autre claim)
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extraire un rôle (ou autre claim) depuis le token
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
    
    public Key getKey() {
        return key;
    }
}

