package com.medilabo.microService.utilisateur.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Gestionnaire global des exceptions pour le microservice utilisateur.
 * <p>
 * Cette classe intercepte les exceptions spécifiques lancées dans les contrôleurs
 * et retourne des réponses HTTP adaptées avec les codes d'état correspondants.
 * </p>
 * 
 * <ul>
 *   <li>Retourne 404 (NOT_FOUND) pour {@link UserNotFoundException}.</li>
 *   <li>Retourne 409 (CONFLICT) pour {@link UsernameAlreadyExistsException}.</li>
 *   <li>Retourne 400 (BAD_REQUEST) pour {@link IllegalArgumentException}.</li>
 *   <li>Retourne 500 (INTERNAL_SERVER_ERROR) pour toutes les autres exceptions.</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions {@link UserNotFoundException}.
     *
     * @param ex l'exception levée
     * @param request la requête en cours
     * @return une réponse HTTP avec le message d'erreur et le statut 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Gère les exceptions {@link UsernameAlreadyExistsException}.
     *
     * @param ex l'exception levée
     * @param request la requête en cours
     * @return une réponse HTTP avec le message d'erreur et le statut 409
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameExists(UsernameAlreadyExistsException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Gère les exceptions {@link IllegalArgumentException}.
     *
     * @param ex l'exception levée
     * @param request la requête en cours
     * @return une réponse HTTP avec le message d'erreur et le statut 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    /**
     * Gère toutes les autres exceptions non prévues.
     * <p>
     * Cette méthode affiche la pile d'erreur dans la console (utile en développement)
     * et renvoie une réponse HTTP avec le statut 500.
     * </p>
     *
     * @param ex l'exception levée
     * @param request la requête en cours
     * @return une réponse HTTP générique avec le message d'erreur et le statut 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex, WebRequest request) {
        ex.printStackTrace(); // utile en dev
        return new ResponseEntity<>("Erreur interne : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
