package de.dhbwravensburg.webeng.pilotlogbook.repository;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.Airport;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class FlightRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private FlightRepository flightRepository;

    @Test
    void findByFilters_scopesByPilotAndAppliesIcaoAndMonth() {
        Pilot pilot1 = em.persist(Pilot.builder()
                .firstName("Anna").lastName("Pilot")
                .email("a@b.de").password("HASH").build());
        Pilot pilot2 = em.persist(Pilot.builder()
                .firstName("Bob").lastName("Pilot")
                .email("b@b.de").password("HASH").build());

        Airport edds = em.persist(Airport.builder()
                .icao("EDDS").name("Stuttgart")
                .latitude(48.69).longitude(9.22).build());
        Airport eddf = em.persist(Airport.builder()
                .icao("EDDF").name("Frankfurt")
                .latitude(50.03).longitude(8.55).build());

        Aircraft aircraft1 = em.persist(Aircraft.builder()
                .pilot(pilot1).registration("D-EABC").type("C172")
                .engineType(Aircraft.EngineType.SINGLE_PISTON).build());
        Aircraft aircraft2 = em.persist(Aircraft.builder()
                .pilot(pilot2).registration("D-EDEF").type("C172")
                .engineType(Aircraft.EngineType.SINGLE_PISTON).build());

        Flight matching = persistFlight(pilot1, aircraft1, edds, eddf,
                LocalDateTime.of(2026, 5, 1, 10, 0));
        Flight wrongMonth = persistFlight(pilot1, aircraft1, edds, eddf,
                LocalDateTime.of(2026, 6, 1, 10, 0));
        Flight wrongDep = persistFlight(pilot1, aircraft1, eddf, edds,
                LocalDateTime.of(2026, 5, 2, 10, 0));
        Flight wrongPilot = persistFlight(pilot2, aircraft2, edds, eddf,
                LocalDateTime.of(2026, 5, 3, 10, 0));

        em.flush();

        List<Flight> result = flightRepository.findByFilters(
                "EDDS", null, null, 5, pilot1.getId(), PageRequest.of(0, 10));

        assertThat(result)
                .extracting(Flight::getId)
                .containsExactly(matching.getId())
                .doesNotContain(wrongMonth.getId(), wrongDep.getId(), wrongPilot.getId());
    }

    private Flight persistFlight(Pilot pilot, Aircraft aircraft, Airport from, Airport to,
                                  LocalDateTime departure) {
        return em.persist(Flight.builder()
                .pilot(pilot).aircraft(aircraft)
                .originAirport(from).destinationAirport(to)
                .departureTime(departure)
                .arrivalTime(departure.plusHours(1))
                .landings(1)
                .pilotFunction(Flight.PilotFunction.PIC)
                .flightType(Flight.FlightType.VFR)
                .build());
    }
}