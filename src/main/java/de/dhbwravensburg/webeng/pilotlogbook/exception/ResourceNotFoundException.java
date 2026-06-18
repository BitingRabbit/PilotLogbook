package de.dhbwravensburg.webeng.pilotlogbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource was not found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
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

