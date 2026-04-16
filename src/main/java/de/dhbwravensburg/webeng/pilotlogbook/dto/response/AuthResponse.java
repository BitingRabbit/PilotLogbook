package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

public class AuthResponse {

    private String tokenType = "Bearer";
    private String token;

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

