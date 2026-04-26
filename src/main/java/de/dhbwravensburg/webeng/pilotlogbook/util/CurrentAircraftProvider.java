package de.dhbwravensburg.webeng.pilotlogbook.util;

import de.dhbwravensburg.webeng.pilotlogbook.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.repository.AircraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves an {@link Aircraft} by ID, enforcing that it belongs to the given pilot.
 */
@Component
@RequiredArgsConstructor
public class CurrentAircraftProvider {

    private final AircraftRepository aircraftRepository;

    /**
     * Returns the aircraft with the given ID if it is owned by the specified pilot.
     *
     * @param aircraftId aircraft ID to look up
     * @param pilotId    ID of the owning pilot
     * @return the matching aircraft
     * @throws ResourceNotFoundException if no aircraft with that ID exists for the pilot
     */
    public Aircraft get(Long aircraftId, Long pilotId) {
        return aircraftRepository.findByIdAndPilotId(aircraftId, pilotId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aircraft with id " + aircraftId + " not found"));
    }
}
