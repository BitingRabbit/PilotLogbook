package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.LoginRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.RegisterRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AuthResponse;
import de.dhbwravensburg.webeng.pilotlogbook.exception.ConflictException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.repository.PilotRepository;
import de.dhbwravensburg.webeng.pilotlogbook.security.JwtService;

import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
/**
 * Handles registration and login business logic
 */
public class AuthService {

    private final PilotRepository pilotRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Checks whether a pilot account exists with given email
     *
     * @param email email to look up
     * @return true if an account with that email exists
     */
    @Transactional(readOnly = true)
    public boolean checkEmailExists(@NonNull String email) {
        return pilotRepository.existsByEmail(email);
    }

    /**
     * Registers a new pilot, hashes password and issues a JWT (registration)
     *
     * @param request validated registration data
     * @return authentication response containing a JWT
     */
    @Transactional
    public AuthResponse register(@NonNull RegisterRequest request) {
        if (pilotRepository.existsByEmail(request.email())) {
            throw new ConflictException("E-Mail already exists!");
        }

        Pilot pilot = new Pilot(
                request.firstName(),
                request.lastName(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        try {
            pilotRepository.save(pilot);
        } catch (DataIntegrityViolationException e) {
            // Handles concurrent registration race
            throw new ConflictException("E-Mail already exists!");
        }

        String token = jwtService.generateToken(pilot);
        return new AuthResponse(token);
    }

    /**
     * Authenticates an existing pilot and issues a JWT (login). Idempotent
     *
     * @param request validated login data
     * @return authentication response containing a JWT
     */
    @Transactional(readOnly = true)
    public AuthResponse login(@NonNull LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Pilot pilot = pilotRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        String token = jwtService.generateToken(pilot);
        return new AuthResponse(token);
    }
}

