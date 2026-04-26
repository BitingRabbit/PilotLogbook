package de.dhbwravensburg.webeng.pilotlogbook.controller;


import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.FlightResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing flight log entries of the authenticated pilot.
 * All endpoints are scoped to the current user — pilots can only access their own flights.
 */
@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    /**
     * Creates a new flight log entry for the authenticated pilot.
     *
     * @param request flight details including aircraft, route, times, and metadata
     * @return the created flight with HTTP 201
     */
    @PostMapping
    public ResponseEntity<FlightResponse> createFlight(
            @Valid @RequestBody CreateFlightRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(flightService.createFlight(request));
    }

    /**
     * Returns all flights for the authenticated pilot, sorted by departure time descending.
     *
     * @return list of flights
     */
    @GetMapping
    public ResponseEntity<List<FlightResponse>> getAllFlights(){
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    /**
     * Returns a single flight by ID, scoped to the authenticated pilot.
     *
     * @param id flight ID
     * @return the flight, or 404 if not found or not owned by the pilot
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlightResponse> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    /**
     * Partially updates a flight. Only provided fields are changed.
     * Arrival time must remain after departure time after applying the update.
     *
     * @param id      flight ID
     * @param request fields to update
     * @return the updated flight
     */
    @PatchMapping("/{id}")
    public ResponseEntity<FlightResponse> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFlightRequest request) {
        return ResponseEntity.ok(flightService.updateFlight(id, request));
    }

    /**
     * Deletes a flight owned by the authenticated pilot.
     *
     * @param id flight ID
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns flights for the authenticated pilot filtered by optional query parameters.
     * All parameters are optional; omitting them returns all flights.
     *
     * @param dep      departure ICAO filter (optional)
     * @param dest     destination ICAO filter (optional)
     * @param duration minimum flight duration filter in minutes (optional)
     * @param month    month number 1–12 filter (optional)
     * @return filtered list of flights
     */
    @GetMapping("/dashboard")
    public ResponseEntity<List<FlightResponse>> getDashboardFlights(
            @RequestParam(required = false) String dep,
            @RequestParam(required = false) String dest,
            @RequestParam(required = false) Long duration,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(flightService.getFlightsForDashboard(dep, dest, duration, month));
    }

}