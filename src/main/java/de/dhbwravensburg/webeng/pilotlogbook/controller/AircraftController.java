package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AircraftResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AircraftService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;


/**
 * REST controller for managing aircraft owned by the authenticated pilot.
 * All endpoints are scoped to the current user: pilots can only access their own aircraft.
 */
@RestController
@RequestMapping("/api/v1/aircraft")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    /**
     * Creates a new aircraft for the authenticated pilot..
     *
     * @param request aircraft details (registration, type, model, engine type)
     * @return the created aircraft with HTTP 201
     */
    @PostMapping
    public ResponseEntity<AircraftResponse> createAircraft(
            @Valid @RequestBody CreateAircraftRequest request) {
        AircraftResponse created = aircraftService.createAircraft(request);
        return ResponseEntity.created(URI.create("/api/v1/aircraft/" + created.id()))
                .body(created);
    }

    /**
     * Returns all aircraft belonging to the authenticated pilot.
     *
     * @return list of aircraft
     */
    @GetMapping
    public ResponseEntity<List<AircraftResponse>> getAllAircraft() {
        return ResponseEntity.ok(aircraftService.getAllAircraft());
    }

    /**
     * Returns a single aircraft by ID, scoped to the authenticated pilot.
     *
     * @param id aircraft ID
     * @return the aircraft, or 404 if not found or not owned by the pilot
     */
    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(@PathVariable Long id) {
        return ResponseEntity.ok(aircraftService.getAircraftById(id));
    }

    /**
     * Partially updates an aircraft. Only provided fields are changed. Idempotent
     *
     * @param id      aircraft ID
     * @param request fields to update
     * @return the updated aircraft
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AircraftResponse> updateAircraft(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAircraftRequest request) {
        return ResponseEntity.ok(aircraftService.updateAircraft(id, request));

    }

    /**
     * Deletes an aircraft. Fails if the aircraft still has associated flights.
     *
     * @param id aircraft ID
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);
        return ResponseEntity.noContent().build();
    }
}
