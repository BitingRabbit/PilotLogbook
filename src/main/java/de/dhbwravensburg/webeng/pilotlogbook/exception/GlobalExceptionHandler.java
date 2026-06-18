package de.dhbwravensburg.webeng.pilotlogbook.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles API errors in one place.
 *
 * Maps exceptions to `ProblemDetail` responses.
 * Keeps field-level validation errors under `fields` for the frontend.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles requests for resources that do not exist.
     *
     * @param ex exception carrying the not-found detail message
     * @return HTTP 404 problem detail with the exception message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles business conflicts such as duplicate registration data.
     *
     * @param ex conflict exception
     * @return HTTP 409 problem detail
     */
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles invalid login credentials.
     *
     * @param ex bad credentials exception
     * @return HTTP 401 problem detail
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    /**
     * Catches all Spring Security {@link AuthenticationException} subtypes
     * that aren't handled more specifically.
     *
     * @param ex authentication exception
     * @return HTTP 401 problem detail
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed");
    }

    /**
     * Handles upstream HTTP error responses (4xx/5xx) from the weather provider.
     *
     * @param ex unavailable exception
     * @return HTTP 502 problem detail
     */
    @ExceptionHandler(WeatherUnavailableException.class)
    public ProblemDetail handleUnavailableWeather(WeatherUnavailableException ex) {
        return problem(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    /**
     * Handles upstream HTTP error responses (4xx/5xx) from the airport provider.
     *
     * @param ex unavailable exception
     * @return HTTP 502 problem detail
     */
    @ExceptionHandler(AirportUnavailableException.class)
    public ProblemDetail handleUnavailableAirport(AirportUnavailableException ex) {
        return problem(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    /**
     * Handles transport-level failures reaching the weather provider
     *
     * @param ex timeout exception
     * @return HTTP 504 problem detail
     */
    @ExceptionHandler(WeatherTimeoutException.class)
    public ProblemDetail handleWeatherTimeout(WeatherTimeoutException ex) {
        return problem(HttpStatus.GATEWAY_TIMEOUT, ex.getMessage());
    }

    /**
     * Handles transport-level failures reaching the airport provider
     * (timeout, connection refused, DNS, ...)
     *
     * @param ex timeout exception
     * @return HTTP 504 problem detail
     */
    @ExceptionHandler(AirportTimeoutException.class)
    public ProblemDetail handleAirportTimeout(AirportTimeoutException ex) {
        return problem(HttpStatus.GATEWAY_TIMEOUT, ex.getMessage());
    }

    /**
     * Handles invalid arguments such as failed ICAO existence checks or
     * arrival-before-departure flight time errors.
     *
     * @param ex illegal argument exception
     * @return HTTP 400 problem detail with the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles validation failures from {@code @RequestParam}/{@code @PathVariable}
     * constraints (e.g. {@code @Pattern}, {@code @Size}) when {@code @Validated} is on the controller.
     *
     * @param ex constraint violation exception
     * @return HTTP 400 problem detail with the violation message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    /**
     * Handles validation failures from {@code @Valid} request bodies. Overrides the
     * base implementation to attach a {@code fields} map ({@code field -> message})
     * to the problem detail
     *
     * @return HTTP 400 problem detail with per-field validation errors
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail body = problem(HttpStatus.BAD_REQUEST, "Validation error");
        body.setTitle("Validation error");
        body.setProperty("fields", fields);

        return handleExceptionInternal(ex, body, headers, status, request);
    }

    // ------------------------ HELPER ------------------------

    /**
     * Builds a {@link ProblemDetail} with the given status and detail message.
     * Spring sets the {@code status} field and the {@code application/problem+json}
     * content type automatically; adds a {@code timestamp}
     *
     * @param status http status code
     * @param detail error message
     * @return populated problem detail
     */
    private ProblemDetail problem(HttpStatus status, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}