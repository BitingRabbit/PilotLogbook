package de.dhbwravensburg.webeng.pilotlogbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the upstream airport provider is unreachable
 * Mapped to HTTP 504 Gateway Timeout by the global handler.
 */
@ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
public class AirportTimeoutException extends RuntimeException {

    /**
     * Creates a new Airport Timeout Exception
     *
     * @param message error details
     */
    public AirportTimeoutException(String message) { super(message); }
}