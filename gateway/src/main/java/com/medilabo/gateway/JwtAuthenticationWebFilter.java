package com.medilabo.gateway;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Filtre Web réactif Spring Security pour authentifier les requêtes HTTP entrantes
 * via un token JWT dans l'en-tête Authorization.
 * <p>
 * Ce filtre extrait le token JWT Bearer de l'en-tête HTTP Authorization,
 * le valide, et si le token est valide, il crée un contexte de sécurité
 * avec l'utilisateur authentifié et son rôle.
 * </p>
 * <p>
 * Le contexte de sécurité est ensuite injecté dans la chaîne réactive
 * pour être utilisé dans les contrôleurs et autres filtres.
 * </p>
 */
@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;


    /**
     * Constructeur avec injection de dépendance du composant JwtUtil.
     * 
     * @param jwtUtil Utilitaire pour manipuler les tokens JWT.
     */
    public JwtAuthenticationWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Filtre chaque requête HTTP entrante pour vérifier la présence et la validité
     * d'un token JWT dans l'en-tête Authorization.
     * <p>
     * Si le token est valide, crée un contexte de sécurité authentifié
     * avec le nom d'utilisateur et le rôle extrait du token.
     * Sinon, laisse passer la requête sans contexte d'authentification.
     * </p>
     * 
     * @param exchange Contient la requête et la réponse HTTP.
     * @param chain Chaîne de filtres Web à appeler après ce filtre.
     * @return Un Mono indiquant la fin du traitement du filtre.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            return chain.filter(exchange);
        }

        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        if (username == null) {
            return chain.filter(exchange);
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(new SecurityContextImpl(auth))));
    }
}
