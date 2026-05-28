package de.dhbwravensburg.webeng.pilotlogbook.repository;

import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class PilotRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PilotRepository pilotRepository;

    @Test
    void findByEmail_returnsPersistedPilotAndEmptyForUnknown() {
        em.persist(Pilot.builder()
                .firstName("Anna").lastName("Pilot")
                .email("known@test.de").password("HASH").build());
        em.flush();

        Optional<Pilot> found = pilotRepository.findByEmail("known@test.de");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("known@test.de");

        assertThat(pilotRepository.findByEmail("unknown@test.de")).isEmpty();
        assertThat(pilotRepository.existsByEmail("known@test.de")).isTrue();
        assertThat(pilotRepository.existsByEmail("unknown@test.de")).isFalse();
    }
}