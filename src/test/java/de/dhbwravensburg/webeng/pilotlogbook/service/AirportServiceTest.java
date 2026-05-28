package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.repository.AirportRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private RestClient ninjaAirportRestClient;

    @InjectMocks
    private AirportService airportService;

    @ParameterizedTest
    @ValueSource(strings = {"EDD", "EDDDS", "ED1S", "ED-S"})
    void findByIcao_rejectsInvalidIcaoFormats(String invalidIcao) {
        assertThatThrownBy(() -> airportService.validateIcaoAndGetOrFetch(invalidIcao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ICAO format");

        verifyNoInteractions(airportRepository, ninjaAirportRestClient);
    }
}