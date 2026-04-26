package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.FlightResponse;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.repository.FlightRepository;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentAircraftProvider;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentFlightProvider;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentPilotProvider;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Creates a new flight log entry for the current pilot.
     * ICAO codes are stored in uppercase. The referenced aircraft must be owned by the pilot.
     *
     * @param request flight details
     * @return the persisted flight
     * @throws ResourceNotFoundException if the referenced aircraft does not belong to the pilot
     */
    @Transactional
    public FlightResponse createFlight(CreateFlightRequest request) {
        Pilot pilot = currentPilotProvider.get();
        Aircraft aircraft = currentAircraftProvider.get(request.getAircraftId(), pilot.getId());

        validateIcao(request.getDepartureIcao());
        validateIcao(request.getDestinationIcao());

        Flight flight = new Flight(
                pilot,
                request.getDepartureIcao().toUpperCase(),
                request.getDestinationIcao().toUpperCase(),
                request.getDepartureTime(),
                request.getArrivalTime(),
                aircraft,
                request.getPassengers(),
                request.getLandings(),
                request.getPilotFunction(),
                request.getFlightType(),
                request.getCost(),
                request.getRemarks()
        );

        return FlightResponse.from(flightRepository.save(flight));
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
     * @param dep      departure ICAO prefix filter (optional)
     * @param dest     destination ICAO prefix filter (optional)
     * @param duration minimum flight duration filter in minutes (optional)
     * @param month    month number filter 1–12 (optional)
     * @return filtered list of flight responses
     */
    @Transactional(readOnly = true)
    public List<FlightResponse> getFlightsForDashboard(
            String dep, String dest, Long duration, Integer month) {

        Pilot pilot = currentPilotProvider.get();

        String depFilter  = (dep != null && !dep.isBlank()) ? dep.trim() : null;
        String destFilter = (dest != null && !dest.isBlank()) ? dest.trim() : null;

        return flightRepository.findByFilters(
                depFilter, destFilter, duration, month, pilot.getId())
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
     * @throws IllegalArgumentException  if the resulting arrival time is not after departure time
     */
    @Transactional
    public FlightResponse updateFlight(Long flightId, UpdateFlightRequest request) {
        Pilot pilot = currentPilotProvider.get();

        Flight flight = currentFlightProvider.get(flightId, pilot.getId());

        if (request.getDepartureIcao() != null)
            flight.setDepartureIcao(request.getDepartureIcao().toUpperCase());

        if (request.getDestinationIcao() != null)
            flight.setDestinationIcao(request.getDestinationIcao().toUpperCase());

        if (request.getDepartureTime() != null)
            flight.setDepartureTime(request.getDepartureTime());

        if (request.getArrivalTime() != null)
            flight.setArrivalTime(request.getArrivalTime());

        if (!flight.getArrivalTime().isAfter(flight.getDepartureTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }

        if (request.getAircraftId() != null) {
            Aircraft aircraft = currentAircraftProvider.get(request.getAircraftId(), pilot.getId());
            flight.setAircraft(aircraft);
        }

        if (request.getPassengers()    != null) flight.setPassengers(request.getPassengers());
        if (request.getLandings()      != null) flight.setLandings(request.getLandings());
        if (request.getPilotFunction() != null) flight.setPilotFunction(request.getPilotFunction());
        if (request.getFlightType()    != null) flight.setFlightType(request.getFlightType());
        if (request.getCost()          != null) flight.setCost(request.getCost());
        if (request.getRemarks()       != null) flight.setRemarks(request.getRemarks());

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



}
