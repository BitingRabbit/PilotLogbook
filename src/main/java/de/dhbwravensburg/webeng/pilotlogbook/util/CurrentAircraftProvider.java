package de.dhbwravensburg.webeng.pilotlogbook.util;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.repository.AircraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentAircraftProvider {

    private final AircraftRepository aircraftRepository;

    public Aircraft get(Long aircraftId, Long pilotId) {
        return aircraftRepository.findByIdAndPilotId(aircraftId, pilotId)
                .orElseThrow(() -> new RuntimeException(
                        "Aircraft with id " + aircraftId + " not found"));
    }
}
