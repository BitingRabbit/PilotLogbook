package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for login authentication
 */
public record LoginRequest(

        @NotBlank(message = "E-Mail empty!")
        @Email(message = "Not a valid email format")
        String email,

        @NotBlank(message = "Password empty!")
        @Size(min = 8, message = "Password needs to be at least 8 characters")
        String password
) {}