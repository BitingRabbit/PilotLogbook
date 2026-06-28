package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.FlightResponse;
import de.dhbwravensburg.webeng.pilotlogbook.model.Airport;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot.PhaseType;
import de.dhbwravensburg.webeng.pilotlogbook.repository.FlightRepository;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentAircraftProvider;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentFlightProvider;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentPilotProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

/**
 * Business logic for flight log CRUD operations.
 * All operations are implicitly scoped to the authenticated pilot via {@link CurrentPilotProvider}.
 */
@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final CurrentPilotProvider currentPilotProvider;
    private final CurrentAircraftProvider currentAircraftProvider;
    private final CurrentFlightProvider currentFlightProvider;
    private final WeatherSnapshotService weatherSnapshotService;
    private final AirportService airportService;

    /**
     * Creates a new flight log entry for the current pilot.
     * ICAO codes are stored in uppercase. The referenced aircraft must be owned by the pilot.
     *
     * @param req flight details
     * @return the persisted flight
     * @throws ResourceNotFoundException if the referenced aircraft does not belong to the pilot
     * @throws IllegalArgumentException if ICAO codes are invalid
     */
    @Transactional
    public FlightResponse createFlight(CreateFlightRequest req) {
        Pilot pilot = currentPilotProvider.get();
        Aircraft aircraft = currentAircraftProvider.get(req.aircraftId(), pilot.getId());

        /* Validate wether ICAO codes are correct (Pattern) and existing */
        Airport dep = airportService.validateIcaoAndGetOrFetch(req.originIcao());
        Airport arr = airportService.validateIcaoAndGetOrFetch(req.destinationIcao());


        Flight flight = Flight.builder()
                .pilot(pilot)
                .originAirport(dep)
                .destinationAirport(arr)
                .departureTime(req.departureTime())
                .arrivalTime(req.arrivalTime())
                .aircraft(aircraft)
                .passengers(req.passengers())
                .landings(req.landings())
                .pilotFunction(req.pilotFunction())
                .flightType(req.flightType())
                .cost(req.cost())
                .remarks(req.remarks())
                .build();

        Flight saved = flightRepository.save(flight);

        // Schedule after commit to prevent race conditions with the async worker
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                weatherSnapshotService.captureSnapshotsForFlight(saved.getId());
            }
        });

        return FlightResponse.from(saved);
    }

    /**
     * Returns a single flight by ID, verifying ownership by the current pilot.
     *
     * @param flightId flight ID
     * @return the flight response
     * @throws ResourceNotFoundException if the flight does not exist or is not owned by the pilot
     */
    @Transactional(readOnly = true)
    public FlightResponse getFlightById(Long flightId) {
        Pilot pilot = currentPilotProvider.get();
        Flight flight = currentFlightProvider.get(flightId, pilot.getId());
        return FlightResponse.from(flight);
    }

    /**
     * Returns all flights for the current pilot, sorted by departure time descending.
     *
     * @return list of flight responses
     */
    @Transactional(readOnly = true)
    public List<FlightResponse> getAllFlights() {
        Pilot pilot = currentPilotProvider.get();
        return flightRepository
                .findByPilotId(pilot.getId())
                .stream()
                .sorted(Comparator.comparing(Flight::getDepartureTime).reversed())
                .map(FlightResponse::from)
                .toList();

    }

    /**
     * Returns flights for the current pilot filtered by optional criteria.
     * Blank strings are treated as not present filters.
     *
     * @param dep      origin ICAO prefix filter (optional)
     * @param dest     destination ICAO prefix filter (optional)
     * @param duration minimum flight duration filter in minutes (optional)
     * @param month    month number filter 1–12 (optional)
     * @return filtered list of flight responses
     */
    @Transactional(readOnly = true)
    public List<FlightResponse> getFlightsForDashboard(
            String dep, String dest, Long duration, Integer month) {

        Pilot pilot = currentPilotProvider.get();

        String depFilter  = (dep != null && !dep.isBlank()) ? dep.trim().toUpperCase() : null;
        String destFilter = (dest != null && !dest.isBlank()) ? dest.trim().toUpperCase() : null;

        return flightRepository.findByFilters(
                depFilter, destFilter, duration, month, pilot.getId(), PageRequest.of(0, 10))
                .stream()
                .map(FlightResponse::from)
                .toList();
    }

    /**
     * Partially updates a flight. Only non-null fields in the request are applied.
     * If arrival or departure time is changed, the arrival must still be after departure.
     *
     * @param flightId flight ID
     * @param request  fields to update
     * @return the updated flight response
     * @throws ResourceNotFoundException if the flight or referenced aircraft does not belong to the pilot
     * @throws IllegalArgumentException  if the resulting arrival time is not after departure time or invalid ICAO codes
     */
    @Transactional
    public FlightResponse updateFlight(Long flightId, UpdateFlightRequest request) {
        Pilot pilot = currentPilotProvider.get();

        Flight flight = currentFlightProvider.get(flightId, pilot.getId());

        if (request.originIcao() != null) {
            /* Validate wether ICAO codes are correct (Pattern) and existing */
            Airport dep = airportService.validateIcaoAndGetOrFetch(request.originIcao());
            flight.setOriginAirport(dep);
            deleteWeatherSnapshot(PhaseType.DEPARTURE, flight);
        }

        if (request.destinationIcao() != null) {
            /* Validate wether ICAO codes are correct (Pattern) and existing */
            Airport arr = airportService.validateIcaoAndGetOrFetch(request.destinationIcao());
            flight.setDestinationAirport(arr);
            deleteWeatherSnapshot(PhaseType.ARRIVAL, flight);
        }
        if (request.departureTime() != null) {
            flight.setDepartureTime(request.departureTime());
            deleteWeatherSnapshot(PhaseType.DEPARTURE, flight);
        }

        if (request.arrivalTime() != null) {
            flight.setArrivalTime(request.arrivalTime());
            deleteWeatherSnapshot(PhaseType.ARRIVAL, flight);
        }

        if (!flight.getArrivalTime().isAfter(flight.getDepartureTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }

        if (request.aircraftId() != null) {
            Aircraft aircraft = currentAircraftProvider.get(request.aircraftId(), pilot.getId());
            flight.setAircraft(aircraft);
        }

        if (request.passengers()    != null) flight.setPassengers(request.passengers());
        if (request.landings()      != null) flight.setLandings(request.landings());
        if (request.pilotFunction() != null) flight.setPilotFunction(request.pilotFunction());
        if (request.flightType()    != null) flight.setFlightType(request.flightType());
        if (request.cost()          != null) flight.setCost(request.cost());
        if (request.remarks()       != null) flight.setRemarks(request.remarks());

        return FlightResponse.from(flightRepository.save(flight));
    }

    /**
     * Deletes a flight owned by the current pilot.
     *
     * @param flightId flight ID
     * @throws ResourceNotFoundException if the flight does not exist or is not owned by the pilot
     */
    @Transactional
    public void deleteFlight(Long flightId) {
        Pilot pilot = currentPilotProvider.get();
        Flight flight = currentFlightProvider.get(flightId, pilot.getId());
        flightRepository.delete(flight);
    }

    // ------------------------ HELPER ------------------------

    private void deleteWeatherSnapshot(PhaseType phaseType, Flight flight) {
        flight.getWeatherSnapshots().removeIf(s -> s.getPhaseType() == phaseType);
    }

}
