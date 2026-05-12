package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.dto.response.MetarDto;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.WeatherSnapshotResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.WeatherService;
import de.dhbwravensburg.webeng.pilotlogbook.service.WeatherSnapshotService;
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
@RequestMapping("/api/v1/metar")
@RequiredArgsConstructor
@Validated
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
    @GetMapping
    public ResponseEntity<MetarDto> getMetar(
            @RequestParam @Pattern(regexp = "^[A-Z]{4}$",
                    message = "ICAO must be exactly 4 letters") String icao,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        String normalized = icao.toUpperCase();
        MetarDto result = (time == null)
                ? weatherService.getLiveMetar(normalized)
                : weatherService.getHistoricalMetar(normalized, time);
        return ResponseEntity.ok(result);
    }

    /**
     * Re-fetches weather snapshots for a flight. snapshots already in {@code AVAILABLE} state
     * remain untouched, only {@code PENDING} or {@code UNAVAILABLE} snapshots are retried.
     *
     * @param flightId id of the flight whose snapshots should be refreshed
     * @return updated departure and arrival snapshot responses
     */
    @PostMapping("/snapshots/refresh/{flightId}")
    public ResponseEntity<List<WeatherSnapshotResponse>> refreshSnapshots(
            @PathVariable Long flightId) {
        return ResponseEntity.ok(weatherSnapshotService.refreshSnapshots(flightId));
    }
}