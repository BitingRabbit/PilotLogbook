package de.dhbwravensburg.webeng.pilotlogbook.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
/**
 * Creates and validates JSON Web Tokens used for stateless authentication
 */
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    /**
     * Creates a JWT service using the signing secret and token lifetime
     *
     * @param secret base64-encoded HMAC secret from the application configuration
     * @param expirationMs token lifetime in milliseconds, default is 24h (=86400000ms)
     */
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a JWT for the authenticated user
     *
     * @param userDetails authenticated user
     * @return signed JWT as string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails);
    }

    /**
     * Builds and signs a JWT containing additional claims for the authenticated user
     *
     * @param claims additional claims to include in the token payload
     * @param userDetails authenticated user
     * @return signed JWT as a string
     */
    public String createToken(Map<String, Object> claims, @NonNull UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts username from a token
     *
     * @param token JWT string
     * @return token subject
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Checks if token matches user and is not expired
     *
     * @param token JWT string
     * @param userDetails user details to compare against
     * @return true when the token is valid for the given user
     */
    public boolean isTokenValid(String token, @NonNull UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

