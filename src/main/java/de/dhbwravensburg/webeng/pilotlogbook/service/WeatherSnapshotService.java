package de.dhbwravensburg.webeng.pilotlogbook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.MetarResponse;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.WeatherSnapshotResponse;
import de.dhbwravensburg.webeng.pilotlogbook.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.pilotlogbook.exception.WeatherTimeoutException;
import de.dhbwravensburg.webeng.pilotlogbook.exception.WeatherUnavailableException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot.PhaseType;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot.Status;
import de.dhbwravensburg.webeng.pilotlogbook.repository.FlightRepository;
import de.dhbwravensburg.webeng.pilotlogbook.repository.WeatherSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherSnapshotService {

    private final FlightRepository flightRepository;
    private final WeatherSnapshotRepository weatherSnapshotRepository;
    private final WeatherService weatherService;
    private final ObjectMapper objectMapper;

    /**
     * Asynchronously captures departure and arrival weather snapshots for the given flight.
     * Each snapshot is saved as PENDING first, then updated to AVAILABLE or UNAVAILABLE
     * depending on whether the noaaWeatherApi returns data. One snapshot failing doesn't affect the other. Idempotent
     *
     * @param flightId id of the flight to capture snapshots for
     */
    @Async
    @Transactional
    public void captureSnapshotsForFlight(Long flightId) {
        // Load directly by ID — security context is not available in async threads
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightId));

        WeatherSnapshot existingDep = weatherSnapshotRepository
                .findByFlightIdAndPhaseType(flightId, PhaseType.DEPARTURE).orElse(null);
        WeatherSnapshot existingArr = weatherSnapshotRepository
                .findByFlightIdAndPhaseType(flightId, PhaseType.ARRIVAL).orElse(null);

        captureOrUpdate(flight, PhaseType.DEPARTURE, flight.getOriginAirport().getIcao(),
                flight.getDepartureTime(), existingDep);
        captureOrUpdate(flight, PhaseType.ARRIVAL, flight.getDestinationAirport().getIcao(),
                flight.getArrivalTime(), existingArr);
    }

    /**
     * Synchronously re-fetches snapshots for a flight where initial async capture failed.
     * Snapshots already in {@link Status#AVAILABLE} are ignored. Idempotent
     *
     * @param flightId id of the flight to refresh
     * @return updated snapshot responses (departure + arrival)
     */
    @Transactional
    public List<WeatherSnapshotResponse> refreshSnapshots(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightId));

        Map<PhaseType, WeatherSnapshot> existing = flight.getWeatherSnapshots().stream()
                .collect(Collectors.toMap(
                        WeatherSnapshot::getPhaseType,
                        s -> s,
                        // Keep the most recently created entry if somehow duplicates exist in DB
                        (a, b) -> a.getId() > b.getId() ? a : b));

        captureOrUpdate(flight, PhaseType.DEPARTURE, flight.getOriginAirport().getIcao(),
                flight.getDepartureTime(), existing.get(PhaseType.DEPARTURE));
        captureOrUpdate(flight, PhaseType.ARRIVAL, flight.getDestinationAirport().getIcao(),
                flight.getArrivalTime(), existing.get(PhaseType.ARRIVAL));

        return weatherSnapshotRepository.findByFlightId(flightId).stream()
                .map(WeatherSnapshotResponse::from)
                .toList();
    }

    // ------------------------ HELPER ------------------------

    /**
     * Creates a new snapshot or updates an existing one with fresh METAR data.
     * If the existing snapshot is already AVAILABLE, it is ignored. Idempotent
     */
    private void captureOrUpdate(Flight flight, PhaseType phase, String icao,
                                  LocalDateTime time, WeatherSnapshot existing) {
        if (existing != null && existing.getStatus() == Status.AVAILABLE) {
            return;
        }

        WeatherSnapshot snapshot = existing;
        if (snapshot == null) {
            snapshot = new WeatherSnapshot(phase, icao);
            flight.addWeatherSnapshot(snapshot);
            snapshot = weatherSnapshotRepository.save(snapshot);
        }

        try {
            MetarResponse metar = weatherService.getHistoricalMetar(icao, time);
            String decodedMetarJson = objectMapper.writeValueAsString(metar.decodedMetar());
            snapshot.markAvailable(metar.rawMetar(), decodedMetarJson);
        } catch (WeatherUnavailableException | WeatherTimeoutException
                 | ResourceNotFoundException | JsonProcessingException e) {
            log.warn("Failed to capture {} snapshot for flight {} ({}): {}",
                    phase, flight.getId(), icao, e.getMessage());
            snapshot.markUnavailable();
        }
    }
}