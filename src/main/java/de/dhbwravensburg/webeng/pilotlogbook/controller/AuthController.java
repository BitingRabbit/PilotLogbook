package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.LoginRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.RegisterRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AuthResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
/*
 * authentication endpoints for registration and login
 */
public class AuthController {

    private final AuthService authService;

    /**
     * Checks whether a pilot account exists with given email
     *
     * @param email email to look up
     * @return JSON with "exists" boolean
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = authService.checkEmailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Registers a new pilot account and returns in the end a signed JWT (Authresponse)
     *
     * @param request validated registration payload
     * @return token response with HTTP 201
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a pilot and returns a signed JWT (Authresponse)
     *
     * @param request validated login payload
     * @return token response with HTTP 200
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

