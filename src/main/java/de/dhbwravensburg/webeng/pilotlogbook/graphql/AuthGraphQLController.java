package de.dhbwravensburg.webeng.pilotlogbook.graphql;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.LoginRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.RegisterRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AuthResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthGraphQLController {

    private final AuthService authService;

    @MutationMapping
    public AuthResponse register(@Argument RegisterRequest input) {
        return authService.register(input);
    }

    @MutationMapping
    public AuthResponse login(@Argument LoginRequest input) {
        return authService.login(input);
    }
}
