package de.dhbwravensburg.webeng.pilotlogbook.exception;

/**
 * Thrown when an operation conflicts with existing data (e.g. duplicate registration)
 */
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