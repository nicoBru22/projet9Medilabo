package com.medilabo.microService.utilisateur.exception;

/**
 * Exception lancée lorsqu'un nom d'utilisateur (username) est déjà utilisé
 * lors de la création ou la mise à jour d'un utilisateur.
 * <p>
 * Il s'agit d'une exception non vérifiée (RuntimeException).
 * </p>
 */
public class UsernameAlreadyExistsException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur avec un message d'erreur personnalisé.
     * 
     * @param message message décrivant la raison de l'exception.
     */
	public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}

