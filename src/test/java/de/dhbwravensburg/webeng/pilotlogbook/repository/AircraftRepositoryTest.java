package de.dhbwravensburg.webeng.pilotlogbook.repository;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class AircraftRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Test
    void existsByRegistrationAndPilotId_isScopedPerPilot() {
        Pilot pilot1 = em.persist(Pilot.builder()
                .firstName("Anna").lastName("Pilot")
                .email("a@b.de").password("HASH").build());
        Pilot pilot2 = em.persist(Pilot.builder()
                .firstName("Bob").lastName("Pilot")
                .email("b@b.de").password("HASH").build());

        em.persist(Aircraft.builder()
                .pilot(pilot1).registration("D-EABC").type("C172")
                .engineType(Aircraft.EngineType.SINGLE_PISTON).build());
        em.persist(Aircraft.builder()
                .pilot(pilot2).registration("D-EABC").type("C172")
                .engineType(Aircraft.EngineType.SINGLE_PISTON).build());
        em.flush();

        assertThat(aircraftRepository.existsByRegistrationAndPilotId("D-EABC", pilot1.getId()))
                .isTrue();
        assertThat(aircraftRepository.existsByRegistrationAndPilotId("D-EABC", pilot2.getId()))
                .isTrue();
        assertThat(aircraftRepository.existsByRegistrationAndPilotId("D-EFGH", pilot1.getId()))
                .isFalse();
    }
}