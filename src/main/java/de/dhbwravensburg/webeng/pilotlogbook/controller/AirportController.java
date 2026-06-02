package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AirportResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/airports")
@RequiredArgsConstructor
@Validated
@Tag(name = "Airports", description = "Public airport master data lookup by ICAO code")
public class AirportController {

    private final AirportService airportService;

    @Operation(
            summary = "Get airport master data by ICAO code",
            description = "Returns name, coordinates, elevation, timezone and runways for the given ICAO. "
                    + "Data is cached locally; on cache miss the upstream provider is queried"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Airport found"),
            @ApiResponse(responseCode = "400", description = "ICAO must be exactly 4 letters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Airport not found", content = @Content),
            @ApiResponse(responseCode = "503", description = "Upstream airport data provider unavailable", content = @Content)
    })
    @GetMapping("/{icao}")
    public ResponseEntity<AirportResponse> getAirportByIcao(
            @Parameter(description = "4-letter ICAO airport code (case-insensitive)", example = "EDDS")
            @PathVariable @Pattern(regexp = "^[A-Za-z]{4}$",
                    message = "ICAO must be exactly 4 letters") String icao) {

        return ResponseEntity.ok(airportService.getByIcao(icao.toUpperCase()));
    }
}