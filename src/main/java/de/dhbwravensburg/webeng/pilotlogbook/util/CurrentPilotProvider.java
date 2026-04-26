package de.dhbwravensburg.webeng.pilotlogbook.util;

import de.dhbwravensburg.webeng.pilotlogbook.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.repository.PilotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves the currently authenticated pilot from the Spring Security context.
 */
@Component
@RequiredArgsConstructor
public class CurrentPilotProvider {

    private final PilotRepository pilotRepository;

    /**
     * Returns the {@link Pilot} entity for the authenticated user.
     * The email is read from the {@link org.springframework.security.core.Authentication} principal.
     *
     * @return the current pilot
     * @throws ResourceNotFoundException if no pilot record exists for the authenticated email
     */
    public Pilot get() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return pilotRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Pilot not found"));
    }
}