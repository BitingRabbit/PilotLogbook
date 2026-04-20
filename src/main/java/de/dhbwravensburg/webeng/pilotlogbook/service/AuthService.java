package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.LoginRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.RegisterRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AuthResponse;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.repository.PilotRepository;
import de.dhbwravensburg.webeng.pilotlogbook.security.JwtService;

import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
/**
 * Handles registration and login business logic
 */
public class AuthService {

    private final PilotRepository pilotRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(PilotRepository pilotRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.pilotRepository = pilotRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new pilot, hashes password and issues a JWT (registration)
     *
     * @param request validated registration data
     * @return authentication response containing a JWT
     */
    public AuthResponse register(@NonNull RegisterRequest request) {
        if (pilotRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("E-Mail already exists!");
        }

        Pilot pilot = new Pilot(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        try {
            pilotRepository.save(pilot);
        } catch (DataIntegrityViolationException e) {
            // Handles concurrent registration race
            throw new IllegalArgumentException("E-Mail already exists!");
        }

        String token = jwtService.generateToken(pilot);
        return new AuthResponse(token);
    }

    /**
     * Authenticates an existing pilot and issues a JWT (login)
     *
     * @param request validated login data
     * @return authentication response containing a JWT
     */
    public AuthResponse login(@NonNull LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Pilot pilot = pilotRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        String token = jwtService.generateToken(pilot);
        return new AuthResponse(token);
    }
}

