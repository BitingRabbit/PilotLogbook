package de.dhbwravensburg.webeng.pilotlogbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an operation conflicts with existing data (e.g. duplicate registration)
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    /**
     * Creates a new conflict exception
     *
     * @param message error details
     */
    public ConflictException(String message) {
        super(message);
    }
}