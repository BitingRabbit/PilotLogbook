package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.Airport;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.repository.FlightRepository;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentAircraftProvider;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentFlightProvider;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentPilotProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock private FlightRepository flightRepository;
    @Mock private CurrentPilotProvider currentPilotProvider;
    @Mock private CurrentAircraftProvider currentAircraftProvider;
    @Mock private CurrentFlightProvider currentFlightProvider;
    @Mock private WeatherSnapshotService weatherSnapshotService;
    @Mock private AirportService airportService;

    @InjectMocks
    private FlightService flightService;

    @Test
    void createFlight_throwsWhenArrivalNotAfterDeparture() {
        Pilot pilot = Pilot.builder()
                .firstName("Anna").lastName("Pilot")
                .email("a@b.de").password("HASH")
                .build();
        Aircraft aircraft = Aircraft.builder()
                .pilot(pilot).registration("D-EABC").type("C172")
                .engineType(Aircraft.EngineType.SINGLE_PISTON)
                .build();
        Airport edds = Airport.builder()
                .icao("EDDS").name("Stuttgart")
                .latitude(48.69).longitude(9.22)
                .build();

        when(currentPilotProvider.get()).thenReturn(pilot);
        when(currentAircraftProvider.get(anyLong(), any())).thenReturn(aircraft);
        when(airportService.validateIcaoAndGetOrFetch("EDDS")).thenReturn(edds);
        when(airportService.validateIcaoAndGetOrFetch("EDDF")).thenReturn(edds);

        LocalDateTime departure = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime arrival = departure.minusHours(1);
        CreateFlightRequest req = new CreateFlightRequest(
                "EDDS", "EDDF", departure, arrival, 1L,
                0, 1, Flight.PilotFunction.PIC, Flight.FlightType.VFR,
                BigDecimal.ZERO, null);

        assertThatThrownBy(() -> flightService.createFlight(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arrival time must be after departure time");

        verify(flightRepository, never()).save(any());
    }
}