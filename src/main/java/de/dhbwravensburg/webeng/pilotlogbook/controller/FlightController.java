package de.dhbwravensburg.webeng.pilotlogbook.controller;


import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.FlightResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing flight log entries of the authenticated pilot.
 * All endpoints are scoped to the current user — pilots can only access their own flights.
 */
@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
@Validated
@Tag(name = "Flights", description = "CRUD operations for the authenticated pilot's flight log entries")
@SecurityRequirement(name = "bearerAuth")
public class FlightController {

    private final FlightService flightService;

    /**
     * Creates a new flight log entry for the authenticated pilot.
     *
     * @param request flight details including aircraft, route, times, and metadata
     * @return the created flight with HTTP 201
     */
    @Operation(
            summary = "Create a flight log entry",
            description = "Persists a new flight for the authenticated pilot. Triggers weather snapshots in the background."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Flight created"),
            @ApiResponse(responseCode = "400", description = "Validation error (e.g. arrival before departure, unknown ICAO)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "404", description = "Referenced aircraft does not exist", content = @Content)
    })
    @PostMapping
    public ResponseEntity<FlightResponse> createFlight(
            @Valid @RequestBody CreateFlightRequest request) {
        FlightResponse created = flightService.createFlight(request);
        return ResponseEntity.created(URI.create("/api/v1/flights/" + created.getId()))
                .body(created);
    }

    /**
     * Returns all flights for the authenticated pilot, sorted by departure time descending.
     *
     * @return list of flights
     */
    @Operation(
            summary = "List all flights",
            description = "Returns every flight owned by the authenticated pilot, sorted by departure time descending."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of flights (may be empty if no registered flights)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content)
    })
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
    @Operation(
            summary = "Get a flight by ID",
            description = "Returns the flight with the given ID, scoped to the authenticated pilot."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flight found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "404", description = "Flight does not exist or is not owned by the pilot", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FlightResponse> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    /**
     * Partially updates a flight. Only provided fields are changed. Idempotent
     * Arrival time must remain after departure time after applying the update.
     *
     * @param id      flight ID
     * @param request fields to update
     * @return the updated flight
     */
    @Operation(
            summary = "Update a flight",
            description = "Partially updates a flight. Only non-null fields are applied. Arrival time must remain after departure time (check)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flight updated"),
            @ApiResponse(responseCode = "400", description = "Validation error (e.g. arrival before departure)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "404", description = "Flight does not exist or is not owned by the pilot", content = @Content)
    })
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
    @Operation(
            summary = "Delete a flight",
            description = "Deletes the flight owned by the authenticated pilot."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Flight deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "404", description = "Flight does not exist or is not owned by the pilot", content = @Content)
    })
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
    @Operation(
            summary = "List flights with dashboard filters",
            description = "Returns flights filtered by optional departure/destination ICAO, minimum duration "
                    + "and month. All parameters are optional; omitting them returns all flights."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered list of flights"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content)
    })
    @GetMapping("/dashboard")
    public ResponseEntity<List<FlightResponse>> getDashboardFlights(
            @Parameter(description = "Departure ICAO filter (4 letters)", example = "EDDS")
            @RequestParam(required = false) String dep,
            @Parameter(description = "Destination ICAO filter (4 letters)", example = "EDDF")
            @RequestParam(required = false) String dest,
            @Parameter(description = "Minimum flight duration in minutes", example = "60")
            @RequestParam(required = false) Long duration,
            @Parameter(description = "Month number 1-12", example = "5")
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(flightService.getFlightsForDashboard(dep, dest, duration, month));
    }

}