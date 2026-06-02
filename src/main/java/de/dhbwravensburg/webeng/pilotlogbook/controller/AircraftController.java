package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AircraftResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AircraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Aircraft", description = "CRUD operations for the authenticated pilot's aircraft fleet")
@SecurityRequirement(name = "bearerAuth")
public class AircraftController {

    private final AircraftService aircraftService;

    /**
     * Creates a new aircraft for the authenticated pilot..
     *
     * @param request aircraft details (registration, type, model, engine type)
     * @return the created aircraft with HTTP 201
     */
    @Operation(
            summary = "Create an aircraft",
            description = "Adds a new aircraft to the authenticated pilot's fleet."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Aircraft created"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content)
    })
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
    @Operation(
            summary = "List all aircraft",
            description = "Returns every aircraft owned by the authenticated pilot."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of aircraft (may be empty)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content)
    })
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
    @Operation(
            summary = "Get an aircraft by ID",
            description = "Returns the aircraft with the given ID, scoped to the authenticated pilot."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aircraft found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aircraft does not exist or is not owned by the pilot", content = @Content)
    })
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
    @Operation(
            summary = "Update an aircraft",
            description = "Partially updates an aircraft. Only non-null fields in the request are applied. Idempotent."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aircraft updated"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aircraft does not exist or is not owned by the pilot", content = @Content)
    })
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
    @Operation(
            summary = "Delete an aircraft",
            description = "Deletes the aircraft. Fails with 409 if the aircraft still has associated flights."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aircraft deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aircraft does not exist or is not owned by the pilot", content = @Content),
            @ApiResponse(responseCode = "409", description = "Aircraft still has associated flights", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);
        return ResponseEntity.noContent().build();
    }
}