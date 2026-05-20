package de.dhbwravensburg.webeng.pilotlogbook.repository;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;


@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Finds a flight by its ID, but only if it belongs to the given pilot.
     * Returns empty when the flight does not exist or belongs to a different pilot.
     *
     * @param flightId unique identifier of the flight
     * @param pilotId  ID of the owning pilot
     * @return the flight wrapped in an Optional, or empty if not found
     */
    Optional<Flight> findByIdAndPilotId(Long flightId, Long pilotId);

    /**
     * Returns all flights belonging to a pilot
     *
     * @param pilotId the pilots id
     * @return list of flights flown by the pilot
     */
    @EntityGraph(attributePaths = { "originAirport", "originAirport.runways", "destinationAirport",
            "destinationAirport.runways", "aircraft", "weatherSnapshots" }
    )
    List<Flight> findByPilotId(Long pilotId);

    /**
     * Returns the last 10 Flights based on the params, all parameters are optional
     * If no params set, will return the 10 latest flights
     *
     * @param dep departure icao code
     * @param dest arrival icao code
     * @param duration duration of the flight in minutes
     * @param month month of the flight (dep)
     * @return list of flights matching the criteria
     */
    @EntityGraph(attributePaths = { "originAirport", "originAirport.runways", "destinationAirport",
            "destinationAirport.runways", "aircraft", "weatherSnapshots" }
    )
    @Query("""
        SELECT f FROM Flight f
        WHERE (:dep IS NULL OR f.originAirport.icao = :dep)
          AND (:dest IS NULL OR f.destinationAirport.icao = :dest)
          AND (:duration IS NULL OR f.durationInMinutes = :duration)
          AND (:month IS NULL OR MONTH(f.departureTime) = :month)
          AND (f.pilot.id = :pilotId)
        ORDER BY f.departureTime DESC
    """)
    List<Flight> findByFilters(
            @Param("dep") String dep,
            @Param("dest") String dest,
            @Param("duration") Long duration,
            @Param("month") Integer month,
            @Param("pilotId") Long pilotId,
            Pageable pageable
    );

    /**
     * Checks whether at least one flight references the given aircraft.
     * Used to prevent deletion of an aircraft that is still in use.
     *
     * @param aircraftId ID of the aircraft to check
     * @return {@code true} if any flight references this aircraft
     */
    boolean existsByAircraftId(Long aircraftId);
}