package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

/**
 * Response payload returned after successful authentication.
 *
 * @param tokenType always {@code "Bearer"}
 * @param token     signed JWT token value
 */
public record AuthResponse(String tokenType, String token) {

    /**
     * Convenience constructor that fixes {@code tokenType} to {@code "Bearer"}.
     *
     * @param token signed JWT token value
     */
    public AuthResponse(String token) {
        this("Bearer", token);
    }
}