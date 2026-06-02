package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload returned after successful authentication.
 *
 * @param tokenType always {@code "Bearer"}
 * @param token     signed JWT token value
 */
@Schema(description = "JWT token response returned by login and register endpoints")
public record AuthResponse(

        @Schema(description = "Always \"Bearer\"", example = "Bearer")
        String tokenType,

        @Schema(description = "Signed JWT (Base64-encoded, at least 32 bytes)", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWVsaWFAZXhhbXBsZS5jb20ifQ.xxxx")
        String token
) {

    /**
     * Convenience constructor that fixes {@code tokenType} to {@code "Bearer"}.
     *
     * @param token signed JWT token value
     */
    public AuthResponse(String token) {
        this("Bearer", token);
    }
}