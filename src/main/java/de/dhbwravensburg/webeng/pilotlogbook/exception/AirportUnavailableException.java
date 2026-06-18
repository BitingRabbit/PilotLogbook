package de.dhbwravensburg.webeng.pilotlogbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the upstream airport provider returned an HTTP error response
 * (4xx/5xx). Mapped to HTTP 502 Bad Gateway by the global handler.
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class AirportUnavailableException extends RuntimeException {

    /**
     * Creates a new Airport Unavailable Exception
     *
     * @param message error details
     */
    public AirportUnavailableException(String message) { super(message); }
}
