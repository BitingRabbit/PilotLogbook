package de.dhbwravensburg.webeng.pilotlogbook.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps backend exceptions to structured HTTP error responses.
 *
 * Each handler method converts a specific exception type into an appropriate
 * HTTP status code and a JSON body containing {@code timestamp}, {@code status},
 * and {@code error} fields.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation failures from @Valid request bodies
     *
     * @param ex validation exception
     * @return HTTP 400 response with field validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation error");
        body.put("fields", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles requests for resources that do not exist.
     *
     * @param ex exception carrying the not-found detail message
     * @return HTTP 404 response with the exception message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles business conflicts such as duplicate registration data
     *
     * @param ex conflict exception
     * @return HTTP 409 response
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles malformed JSON or invalid enum values in request bodies
     *
     * @param ex deserialization exception
     * @return HTTP 400 response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body");
    }

    /**
     * Handles invalid login credentials
     *
     * @param ex bad credentials exception
     * @return HTTP 401 response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    /**
     * Handles unavailable weather data issue
     *
     * @param ex unavailable exception
     * @return HTTP 503 response
     */
    @ExceptionHandler(WeatherUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleUnavailableWeather(WeatherUnavailableException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Weather data currently unavailable");
    }

    /**
     * Handles invalid arguments such as failed ICAO existence checks or
     * arrival-before-departure flight time errors.
     *
     * @param ex illegal argument exception
     * @return HTTP 400 response with the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles validation failures from {@code @RequestParam}/{@code @PathVariable}
     * constraints (e.g. {@code @Pattern}, {@code @Size}) when {@code @Validated} is on the controller.
     *
     * @param ex constraint violation exception
     * @return HTTP 400 response with the violation message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ------------------------ HELPER ------------------------

    /**
     * helper to avoid redundancy, builds an exception response
     *
     * @param status  http Status Code
     * @param message error message
     * @return http error response
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}

