package de.dhbwravensburg.webeng.pilotlogbook.util;

import de.dhbwravensburg.webeng.pilotlogbook.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Resolves a {@link Flight} by ID, enforcing that it belongs to the given pilot.
 */
@Component
@RequiredArgsConstructor
public class CurrentFlightProvider {

    private final FlightRepository flightRepository;

    /**
     * Returns the flight with the given ID if it is owned by the specified pilot.
     *
     * @param flightId flight ID to look up
     * @param pilotId  ID of the owning pilot
     * @return the matching flight
     * @throws ResourceNotFoundException if no flight with that ID exists for the pilot
     */
    public Flight get(Long flightId, Long pilotId) {
        return flightRepository.findByIdAndPilotId(flightId, pilotId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Flight with id " + flightId + " not found"));

    }
}
