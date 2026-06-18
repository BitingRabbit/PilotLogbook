package de.dhbwravensburg.webeng.pilotlogbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the upstream weather provider returned an HTTP error response
 * (4xx/5xx). Mapped to HTTP 502 Bad Gateway by the global handler.
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class WeatherUnavailableException extends RuntimeException {
    /**
     * Creates a new Weather Unavailable Exception
     *
     * @param message error details
     */
    public WeatherUnavailableException(String message) { super(message); }
}