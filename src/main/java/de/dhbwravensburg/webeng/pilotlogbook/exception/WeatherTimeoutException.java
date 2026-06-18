package de.dhbwravensburg.webeng.pilotlogbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the upstream weather provider is unreachable
 * Mapped to HTTP 504 Gateway Timeout by the global handler.
 */
@ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
public class WeatherTimeoutException extends RuntimeException {

    /**
     * Creates a new Weather Timeout Exception
     *
     * @param message error details
     */
    public WeatherTimeoutException(String message) { super(message); }
}