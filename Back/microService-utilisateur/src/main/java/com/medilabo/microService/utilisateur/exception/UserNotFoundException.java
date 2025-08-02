package com.medilabo.microService.utilisateur.exception;

/**
 * Exception lancée lorsqu'un utilisateur recherché n'a pas été trouvé
 * dans la base de données ou le système.
 * <p>
 * Cette exception est une exception non vérifiée (RuntimeException).
 * </p>
 */
public class UserNotFoundException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur avec un message d'erreur personnalisé.
     * 
     * @param message message décrivant la raison de l'exception.
     */
	public UserNotFoundException(String message) {
        super(message);
    }
}

