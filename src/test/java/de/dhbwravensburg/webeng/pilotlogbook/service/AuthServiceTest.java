package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.RegisterRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AuthResponse;
import de.dhbwravensburg.webeng.pilotlogbook.exception.ConflictException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.repository.PilotRepository;
import de.dhbwravensburg.webeng.pilotlogbook.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PilotRepository pilotRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_throwsConflictWhenEmailExists() {
        RegisterRequest req = new RegisterRequest("Anna", "Pilot", "anna@test.de", "Secret123");
        when(pilotRepository.existsByEmail("anna@test.de")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("E-Mail already exists");

        verify(pilotRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void register_hashesPasswordAndReturnsToken() {
        RegisterRequest req = new RegisterRequest("Anna", "Pilot", "anna@test.de", "Secret123");
        when(pilotRepository.existsByEmail("anna@test.de")).thenReturn(false);
        when(passwordEncoder.encode("Secret123")).thenReturn("HASHED");
        when(jwtService.generateToken(any(Pilot.class))).thenReturn("jwt-xyz");

        AuthResponse response = authService.register(req);

        ArgumentCaptor<Pilot> captor = ArgumentCaptor.forClass(Pilot.class);
        verify(pilotRepository).save(captor.capture());
        Pilot saved = captor.getValue();

        assertThat(saved.getPassword()).isEqualTo("HASHED");
        assertThat(saved.getPassword()).isNotEqualTo("Secret123");
        assertThat(saved.getEmail()).isEqualTo("anna@test.de");
        assertThat(response.token()).isEqualTo("jwt-xyz");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }
}