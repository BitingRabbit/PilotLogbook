package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request payload for login authentication
 */
public class LoginRequest {

    @NotBlank(message = "E-Mail empty!")
    @Email(message = "Not a valid email format")
    private String email;

    @NotBlank(message = "Password empty!")
    @Size(min = 8, message = "Password needs to be at least 8 characters")
    private String password;

    public LoginRequest() {
    }

    /**
     * Creates a login payload
     *
     * @param email login email
     * @param password password from client
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

