package de.dhbwravensburg.webeng.pilotlogbook.exception;

/**
 * Thrown when the airport service cannot deliver airport data or is unreachable
 */
public class AirportUnavailableException extends RuntimeException {

    /**
     * Creates a new Airport Unavailable Exception
     *
     * @param message error details
     */
    public AirportUnavailableException(String message) { super(message); }
}
