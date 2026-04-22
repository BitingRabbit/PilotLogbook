package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

/**
 * Response payload returned after successful authentication
 */
public class AuthResponse {

    private String tokenType = "Bearer";
    private String token;

    /**
     * Creates an authentication response
     *
     * @param token signed JWT token value
     */
    public AuthResponse(String token) {
        this.token = token;
    }

    public String getTokenType() { return tokenType; }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

