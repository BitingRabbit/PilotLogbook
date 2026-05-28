package de.dhbwravensburg.webeng.pilotlogbook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.Airport;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot.PhaseType;
import de.dhbwravensburg.webeng.pilotlogbook.repository.FlightRepository;
import de.dhbwravensburg.webeng.pilotlogbook.repository.WeatherSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherSnapshotServiceTest {

    @Mock private FlightRepository flightRepository;
    @Mock private WeatherSnapshotRepository weatherSnapshotRepository;
    @Mock private WeatherService weatherService;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private WeatherSnapshotService weatherSnapshotService;

    @Test
    void captureSnapshot_skipsWhenAlreadyAvailableForBothPhases() {
        Pilot pilot = Pilot.builder()
                .firstName("Anna").lastName("Pilot")
                .email("a@b.de").password("HASH").build();
        Aircraft aircraft = Aircraft.builder()
                .pilot(pilot).registration("D-EABC").type("C172")
                .engineType(Aircraft.EngineType.SINGLE_PISTON).build();
        Airport edds = Airport.builder()
                .icao("EDDS").name("Stuttgart")
                .latitude(48.69).longitude(9.22).build();
        Airport eddf = Airport.builder()
                .icao("EDDF").name("Frankfurt")
                .latitude(50.03).longitude(8.55).build();
        Flight flight = Flight.builder()
                .pilot(pilot).aircraft(aircraft)
                .originAirport(edds).destinationAirport(eddf)
                .departureTime(LocalDateTime.of(2026, 5, 1, 10, 0))
                .arrivalTime(LocalDateTime.of(2026, 5, 1, 11, 30))
                .landings(1)
                .pilotFunction(Flight.PilotFunction.PIC)
                .flightType(Flight.FlightType.VFR)
                .build();

        WeatherSnapshot existingDep = new WeatherSnapshot(PhaseType.DEPARTURE, "EDDS");
        existingDep.markAvailable("METAR EDDS ...", "{}");
        WeatherSnapshot existingArr = new WeatherSnapshot(PhaseType.ARRIVAL, "EDDF");
        existingArr.markAvailable("METAR EDDF ...", "{}");

        when(flightRepository.findById(42L)).thenReturn(Optional.of(flight));
        when(weatherSnapshotRepository.findByFlightIdAndPhaseType(42L, PhaseType.DEPARTURE))
                .thenReturn(Optional.of(existingDep));
        when(weatherSnapshotRepository.findByFlightIdAndPhaseType(42L, PhaseType.ARRIVAL))
                .thenReturn(Optional.of(existingArr));

        weatherSnapshotService.captureSnapshotsForFlight(42L);

        verifyNoInteractions(weatherService);
        verify(weatherSnapshotRepository, never()).save(any());
    }
}