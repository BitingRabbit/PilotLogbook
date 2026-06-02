package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.LoginRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.RegisterRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AuthResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * authentication endpoints for registration and login
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Pilot registration, login and email lookup. All endpoints are public.")
public class AuthController {

    private final AuthService authService;

    /**
     * Checks whether a pilot account exists with given email. Idempotent
     *
     * @param email email to look up
     * @return JSON with "exists" boolean
     */
    @Operation(
            summary = "Check whether an email is already registered",
            description = "Idempotent lookup used by the registration form to give early feedback."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lookup result wrapped as { \"exists\": boolean }",
            content = @Content(schema = @Schema(example = "{\"exists\": true}"))
    )
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(
            @Parameter(description = "Email address to look up", example = "pilot@example.com")
            @RequestParam String email) {
        boolean exists = authService.checkEmailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Registers a new pilot account and returns in the end a signed JWT (Authresponse)
     *
     * @param request validated registration payload
     * @return token response with HTTP 201
     */
    @Operation(
            summary = "Register a new pilot account",
            description = "Creates a pilot account and returns a signed JWT. Email must be unique."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created, JWT returned"),
            @ApiResponse(responseCode = "400", description = "Validation error (e.g. weak password, malformed email)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a pilot and returns a signed JWT (Authresponse). Idempotent
     *
     * @param request validated login payload
     * @return token response with HTTP 200
     */
    @Operation(
            summary = "Log in with email and password",
            description = "Verifies credentials and returns a signed JWT on success."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT returned"),
            @ApiResponse(responseCode = "400", description = "Validation error in request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}