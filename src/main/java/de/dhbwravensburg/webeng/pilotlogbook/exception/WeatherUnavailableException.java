package de.dhbwravensburg.webeng.pilotlogbook.exception;

/**
 * Thrown when the upstream weather service cannot deliver METAR data,
 * e.g. on network errors, timeouts, upstream 5xx responses, or empty results
 * for the requested ICAO/time. Mapped to HTTP 503 by the global handler.
 */
public class WeatherUnavailableException extends RuntimeException {
    /**
     * Creates a new Weather Unavailable Exception
     *
     * @param message error details
     */
    public WeatherUnavailableException(String message) { super(message); }
}
