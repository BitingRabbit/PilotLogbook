package de.dhbwravensburg.webeng.pilotlogbook.repository;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    /**
     * Returns all aircraft belonging to a pilot
     *
     * @param pilotId the pilots id
     * @return list of aircraft owned by the pilot
     */
    List<Aircraft> findByPilotId(Long pilotId);

    /**
     * Finds an aircraft by id, but only if it belongs to the given pilot.
     * Combines lookup and ownership check in one query.
     *
     * @param id       the aircrafts id
     * @param pilotId  the pilots id
     * @return specific Aircraft owned by pilpt
     */
    Optional<Aircraft> findByIdAndPilotId(Long id, Long pilotId);

    /**
     * Checks whether a registration already exists for this pilot
     * Used before creating to avoid duplicates
     *
     * @param registration the registration to check for, e.g. "D-EDAB"
     * @param pilotId      the pilots id
     * @return true if already taken
     */
    boolean existsByRegistrationAndPilotId(String registration, Long pilotId);
}
