package com.medilabo.microService.utilisateur.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtre d'authentification JWT qui s'exécute une fois par requête HTTP.
 * <p>
 * Ce filtre intercepte chaque requête entrante et vérifie la présence d'un token JWT dans
 * l'en-tête HTTP "Authorization". Si un token valide est trouvé, il extrait les informations
 * d'authentification (nom d'utilisateur et rôle) et configure le contexte de sécurité Spring
 * avec un objet Authentication.
 * </p>
 * <p>
 * Cela permet de sécuriser les endpoints en s'appuyant sur les informations contenues dans le JWT,
 * sans avoir besoin d'une session côté serveur.
 * </p>
 * 
 * @see JwtUtil pour la gestion des tokens JWT (validation, extraction des claims)
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Constructeur avec injection de dépendance du composant JwtUtil.
     * 
     * @param jwtUtil composant utilitaire pour manipuler les tokens JWT
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Filtre la requête HTTP pour extraire et valider le token JWT.
     * <p>
     * Si un token valide est présent dans l'en-tête "Authorization" (format "Bearer {token}"),
     * il configure le contexte de sécurité avec un objet Authentication contenant
     * le nom d'utilisateur et le rôle extraits du token.
     * </p>
     * <p>
     * La chaîne de filtres continue ensuite son traitement.
     * </p>
     * 
     * @param request requête HTTP entrante
     * @param response réponse HTTP
     * @param filterChain chaîne de filtres à poursuivre après ce filtre
     * @throws ServletException en cas d'erreur du servlet
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority(role))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
