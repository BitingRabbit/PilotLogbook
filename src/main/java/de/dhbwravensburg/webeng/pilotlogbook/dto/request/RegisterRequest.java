package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * Request payload for creating a new account (registration)
 */
public class RegisterRequest {

    @NotBlank(message = "Prename empty!")
    private String firstName;

    @NotBlank(message = "Last Name empty!")
    private String lastName;

    @NotBlank(message = "E-Mail empty!")
    @Email(message = "Not a valid email format")
    private String email;

    @NotBlank(message = "Password empty!")
    @Size(min = 8, message = "Password needs to be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Password must contain upper, lower case letters and a number"
    )
    private String password;

    public RegisterRequest() {
    }

    /**
     * Creates a registration payload
     *
     * @param firstName pilot first name
     * @param lastName pilot last name
     * @param email login email
     * @param password password from client
     */
    public RegisterRequest(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

