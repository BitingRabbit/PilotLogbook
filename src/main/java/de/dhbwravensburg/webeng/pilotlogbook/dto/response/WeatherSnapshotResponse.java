package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot.PhaseType;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot.Status;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.MetarDto.DecodedMetar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Response payload for weather snapshot endpoints.
 * Contains the phase ({@code DEPARTURE} / {@code ARRIVAL}), the fetch status,
 * and (when available) the full parsed METAR with raw string and decoded fields.
 */
@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeatherSnapshotResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private PhaseType phase;
    private Status status;
    private MetarDto metar;  // null if PENDING or UNAVAILABLE

    /**
     * Maps a {@link WeatherSnapshot} entity to this response DTO.
     * When the snapshot is AVAILABLE, the stored JSON is deserialized back to {@link DecodedMetar}.
     * When PENDING or UNAVAILABLE, {@code metar} is {@code null}.
     *
     * @param snapshot the entity to map
     * @return mapped response
     */
    public static WeatherSnapshotResponse from(WeatherSnapshot snapshot) {
        if (snapshot.getStatus() != Status.AVAILABLE) {
            return new WeatherSnapshotResponse(snapshot.getPhaseType(), snapshot.getStatus(), null);
        }

        try {
            DecodedMetar decoded = MAPPER.readValue(
                    snapshot.getDecodedMetarJson(), DecodedMetar.class);
            MetarDto metar = new MetarDto(snapshot.getIcao(), null, snapshot.getRawMetar(), decoded);
            return new WeatherSnapshotResponse(snapshot.getPhaseType(), snapshot.getStatus(), metar);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize decoded METAR JSON for snapshot {}: {}",
                    snapshot.getId(), e.getMessage());
            return new WeatherSnapshotResponse(snapshot.getPhaseType(), Status.UNAVAILABLE, null);
        }
    }
}