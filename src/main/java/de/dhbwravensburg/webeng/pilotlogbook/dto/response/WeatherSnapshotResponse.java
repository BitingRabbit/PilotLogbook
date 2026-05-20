package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.MetarDto.DecodedMetar;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot.PhaseType;
import de.dhbwravensburg.webeng.pilotlogbook.model.WeatherSnapshot.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response payload for weather snapshot endpoints.
 * Contains the phase ({@code DEPARTURE} / {@code ARRIVAL}), the fetch status,
 * and (when available) the full parsed METAR with raw string and decoded fields.
 *
 * @param phase  phase of the flight this snapshot was captured for
 * @param status current processing status of this snapshot
 * @param icao   ICAO code of the observed airport (e.g. {@code EDDS}); always present
 * @param metar  full METAR data; {@code null} when status is PENDING or UNAVAILABLE
 */
public record WeatherSnapshotResponse(
        PhaseType phase,
        Status status,
        String icao,
        MetarDto metar
) {

    private static final Logger log = LoggerFactory.getLogger(WeatherSnapshotResponse.class);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

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
            return new WeatherSnapshotResponse(snapshot.getPhaseType(), snapshot.getStatus(),
                    snapshot.getIcao(), null);
        }

        try {
            DecodedMetar decoded = MAPPER.readValue(
                    snapshot.getDecodedMetarJson(), DecodedMetar.class);
            MetarDto metar = new MetarDto(snapshot.getIcao(), null, snapshot.getRawMetar(), decoded);
            return new WeatherSnapshotResponse(snapshot.getPhaseType(), snapshot.getStatus(),
                    snapshot.getIcao(), metar);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize decoded METAR JSON for snapshot {}: {}",
                    snapshot.getId(), e.getMessage());
            return new WeatherSnapshotResponse(snapshot.getPhaseType(), Status.UNAVAILABLE,
                    snapshot.getIcao(), null);
        }
    }
}