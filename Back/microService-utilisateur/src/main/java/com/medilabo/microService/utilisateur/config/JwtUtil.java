package com.medilabo.microService.utilisateur.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

	private static final String SECRET_KEY = "fF3uM8XbZ9LpQwErTyUiOpAsDfGhJkL1";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 heures

    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

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
    
    public Key getKey() {
        return key;
    }
    
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
    
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

}

