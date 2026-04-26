package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AircraftResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AircraftService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/aircraft")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    @PostMapping
    public ResponseEntity<AircraftResponse> createAircraft(
            @Valid @RequestBody CreateAircraftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aircraftService.createAircraft(request));
    }

    @GetMapping
    public ResponseEntity<List<AircraftResponse>> getAllAircraft() {
        return ResponseEntity.ok(aircraftService.getAllAircraft());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(@PathVariable Long id) {
        return ResponseEntity.ok(aircraftService.getAircraftById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AircraftResponse> updateAircraft(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAircraftRequest request) {
        return ResponseEntity.ok(aircraftService.updateAircraft(id, request));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);
        return ResponseEntity.noContent().build();
    }
}
