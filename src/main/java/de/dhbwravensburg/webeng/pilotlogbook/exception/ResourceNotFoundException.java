package de.dhbwravensburg.webeng.pilotlogbook.exception;

/**
 * Thrown when a requested resource was not found
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new not-found exception
     *
     * @param message error details
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

