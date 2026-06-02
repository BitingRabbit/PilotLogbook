package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for login authentication
 */
@Schema(description = "Login credentials")
public record LoginRequest(

        @Schema(description = "Registered email address", example = "pilot@example.com")
        @NotBlank(message = "E-Mail empty!")
        @Email(message = "Not a valid email format")
        String email,

        @Schema(description = "Password (min 8 characters)", example = "Secret123")
        @NotBlank(message = "Password empty!")
        @Size(min = 8, message = "Password needs to be at least 8 characters")
        String password
) {}