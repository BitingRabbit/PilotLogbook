package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for new account registration.
 */
@Schema(description = "Payload for creating a new pilot account")
public record RegisterRequest(

        @Schema(description = "Pilot first name", example = "Max")
        @NotBlank(message = "First name empty!")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @Schema(description = "Pilot last name", example = "Mustermann")
        @NotBlank(message = "Last name empty!")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @Schema(description = "Email address, must be unique", example = "email@example.com")
        @NotBlank(message = "E-mail empty!")
        @Email(message = "Not a valid email format")
        @Size(max = 100, message = "E-mail must not exceed 100 characters")
        String email,

        @Schema(
                description = "Password: at least 8 characters, must contain uppercase, lowercase and a digit",
                example = "Password123"
        )
        @NotBlank(message = "Password empty!")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                message = "Password must contain uppercase, lowercase letters and a number"
        )
        String password
) {}