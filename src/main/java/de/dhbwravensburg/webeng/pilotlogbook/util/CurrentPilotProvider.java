package de.dhbwravensburg.webeng.pilotlogbook.util;

import de.dhbwravensburg.webeng.pilotlogbook.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.repository.PilotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentPilotProvider {

    private final PilotRepository pilotRepository;

    public Pilot get() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return pilotRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Pilot not found"));
    }
}