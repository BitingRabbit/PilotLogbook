package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AirportResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AirportService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/airports")
@RequiredArgsConstructor
@Validated
public class AirportController {

    private final AirportService airportService;

    @GetMapping("/{icao}")
    public ResponseEntity<AirportResponse> getAirportByIcao(
            @PathVariable @Pattern(regexp = "^[A-Za-z]{4}$",
                    message = "ICAO must be exactly 4 letters") String icao) {

        return ResponseEntity.ok(airportService.getByIcao(icao.toUpperCase()));
    }
}
