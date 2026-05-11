package de.dhbwravensburg.webeng.pilotlogbook.repository;

import de.dhbwravensburg.webeng.pilotlogbook.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    /**
     * Finds an airport by its uppercase ICAO code.
     *
     * @param icao 4-letter uppercase ICAO code
     * @return matching airport, if cached
     */
    Optional<Airport> findByIcao(String icao);

    /**
     * Bulk lookup for the dashboard map. Callers are expected to pass already
     * uppercase, pilot-scoped ICAOs collected from filtered flights.
     *
     * @param icaos uppercase ICAO codes
     * @return matching cached airports
     */
    List<Airport> findByIcaoIn(Collection<String> icaos);
}