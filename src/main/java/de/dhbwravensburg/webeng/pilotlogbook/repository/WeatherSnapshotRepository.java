package de.dhbwravensburg.webeng.pilotlogbook.repository;

import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Persistence access for {@link WeatherSnapshot} entities.
 */
public interface WeatherSnapshotRepository extends JpaRepository<WeatherSnapshot, Long> {

    /**
     * Returns all snapshots belonging to the given flight, regardless of their
     * {@link WeatherSnapshot.Status}.
     *
     * @param flightId id of the owning flight
     * @return snapshots for the flight, empty list if none exist
     */
    List<WeatherSnapshot> findByFlightId(Long flightId);
}