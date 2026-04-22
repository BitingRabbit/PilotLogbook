package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for login authentication
 */

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "E-Mail empty!")
    @Email(message = "Not a valid email format")
    private String email;

    @NotBlank(message = "Password empty!")
    @Size(min = 8, message = "Password needs to be at least 8 characters")
    private String password;
}

