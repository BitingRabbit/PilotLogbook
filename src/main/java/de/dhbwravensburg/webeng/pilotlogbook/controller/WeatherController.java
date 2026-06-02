package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.response.MetarResponse;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.WeatherSnapshotResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.WeatherService;
import de.dhbwravensburg.webeng.pilotlogbook.service.WeatherSnapshotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST endpoints for METAR weather data and per-flight weather snapshots.
 */
@RestController
@RequestMapping("/api/v1/metars")
@RequiredArgsConstructor
@Validated
@Tag(name = "Weather", description = "METAR observations and per-flight weather snapshots")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherSnapshotService weatherSnapshotService;

    /**
     * Returns a METAR for the given airport. If {@code time} is not passed, the most recent
     * METAR is returned, otherwise the observation closest to the given UTC time
     *
     * @param icao 4-letter airport code (case-insensitive, normalised to uppercase)
     * @param time optional ISO-8601 date-time, interpreted as UTC
     * @return live or historical METAR
     */
    @Operation(
            summary = "Get a METAR observation",
            description = "Returns the most recent METAR for the given ICAO when no time is passed, "
                    + "otherwise the observation closest to the given UTC time. Public endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "METAR returned"),
            @ApiResponse(responseCode = "400", description = "ICAO must be exactly 4 letters", content = @Content),
            @ApiResponse(responseCode = "503", description = "Upstream weather provider unavailable", content = @Content)
    })
    @GetMapping
    public ResponseEntity<MetarResponse> getMetar(
            @Parameter(description = "4-letter ICAO code", example = "EDDS")
            @RequestParam @Pattern(regexp = "^[A-Z]{4}$",
                    message = "ICAO must be exactly 4 letters") String icao,
            @Parameter(description = "Optional ISO-8601 UTC time of the observation", example = "2026-05-27T14:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        String normalized = icao.toUpperCase();
        MetarResponse result = (time == null)
                ? weatherService.getLiveMetar(normalized)
                : weatherService.getHistoricalMetar(normalized, time);
        return ResponseEntity.ok(result);
    }

    /**
     * Re-fetches weather snapshots for a flight. snapshots already in {@code AVAILABLE} state
     * remain untouched, only {@code PENDING} or {@code UNAVAILABLE} snapshots are retried. Idempotent
     *
     * @param flightId id of the flight whose snapshots should be refreshed
     * @return updated departure and arrival snapshot responses
     */
    @Operation(
            summary = "Refresh weather snapshots for a flight",
            description = "Re-fetches snapshots in PENDING or UNAVAILABLE state. AVAILABLE snapshots stay untouched. Idempotent."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Departure and arrival snapshots returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "404", description = "Flight does not exist or is not owned by the pilot", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/snapshots/refresh/{flightId}")
    public ResponseEntity<List<WeatherSnapshotResponse>> refreshSnapshots(
            @Parameter(description = "ID of the flight whose snapshots should be refreshed", example = "42")
            @PathVariable Long flightId) {
        return ResponseEntity.ok(weatherSnapshotService.refreshSnapshots(flightId));
    }
}