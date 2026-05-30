package de.dhbwravensburg.webeng.pilotlogbook.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String BASE64_SECRET =
            "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFh";

    private final JwtService jwtService = new JwtService(BASE64_SECRET, 60_000L);

    @Test
    void generateAndExtract_roundTrip() {
        UserDetails user = new User("pilot@test.de", "pw", Collections.emptyList());

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("pilot@test.de");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }
}