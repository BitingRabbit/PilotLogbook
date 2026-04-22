package de.dhbwravensburg.webeng.pilotlogbook.repository;

import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Pilot entities
 */
@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {

    /**
     * Finds a pilot by email address
     *
     * @param email unique email used as username
     * @return pilot wrapped in Optional when found
     */
    Optional<Pilot> findByEmail(String email);

    /**
     * Checks whether a pilot with the given email already exists
     *
     * @param email unique email used as username
     * @return true when a Pilot exists
     */
    boolean existsByEmail(String email);
}

