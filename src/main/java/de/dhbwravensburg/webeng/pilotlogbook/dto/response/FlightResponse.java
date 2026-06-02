package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response payload returned when reading aircraft data.
 *
 * <p>Contains only fields exposed by the API.
 * Use {@link #from(Flight)} to create an instance from a JPA entity.
 * Aircraft details are inlined to avoid a separate lookup on the client side.
 */
@Getter
@Builder
@Schema(description = "Flight log entry with inlined aircraft, airport and weather data")
public class FlightResponse {

    @Schema(description = "Database identifier", example = "42")
    private Long id;

    /** Date and time of departure. */
    @Schema(description = "Date and time of departure", example = "2026-05-27T10:30:00")
    private LocalDateTime departureTime;

    /** Date and time of arrival. */
    @Schema(description = "Date and time of arrival", example = "2026-05-27T12:00:00")
    private LocalDateTime arrivalTime;

    /** Flight duration in minutes, derived from departure and arrival time. */
    @Schema(description = "Flight duration in minutes, derived from departure and arrival", example = "90")
    private Long durationInMinutes;

    /** ID of the aircraft used for this flight. */
    @Schema(description = "ID of the aircraft used for this flight", example = "1")
    private Long aircraftId;

    /** Registration number of the aircraft (e.g. D-ABCD). */
    @Schema(description = "Aircraft registration", example = "D-ABCD")
    private String aircraftRegistration;

    /** Category or type designator of the aircraft. */
    @Schema(description = "ICAO type designator of the aircraft", example = "C172")
    private String aircraftType;

    /** Specific model name of the aircraft. */
    @Schema(description = "Model name of the aircraft", example = "Skyhawk")
    private String aircraftModel;

    /** Number of passengers on board. */
    @Schema(description = "Number of passengers on board", example = "2")
    private Integer passengers;

    /** Number of landings performed. */
    @Schema(description = "Number of landings performed", example = "1")
    private Integer landings;

    /** Role of the pilot during this flight. */
    @Schema(description = "Pilot role during this flight")
    private PilotFunction pilotFunction;

    /** Flight rules category (VFR or IFR). */
    @Schema(description = "Flight rules category (VFR or IFR)")
    private FlightType flightType;

    /** Cost associated with this flight, if recorded. */
    @Schema(description = "Cost associated with this flight in EUR", example = "120.50")
    private BigDecimal cost;

    /** Free-text remarks entered by the pilot. */
    @Schema(description = "Free-text remarks", example = "Crosswind training")
    private String remarks;

    /** Timestamp when this flight entry was created. */
    @Schema(description = "Timestamp when the entry was created", example = "2026-05-27T18:42:00")
    private LocalDateTime createdAt;

    /** Weather snapshots captured at departure and arrival. Empty if not yet fetched. */
    @Schema(description = "Weather snapshots captured at departure and arrival. Empty if not yet fetched.")
    private List<WeatherSnapshotResponse> weatherSnapshots;

    @Schema(description = "Departure airport master data")
    private AirportResponse originAirport;

    @Schema(description = "Destination airport master data")
    private AirportResponse destinationAirport;


    /**
     * Maps a {@link Flight} entity to this {@link FlightResponse} DTO.
     *
     * @param flight flight entity to map, must not be {@Code null}
     * @return new {@code FlightResponse} with mapped values
     */
    public static FlightResponse from(Flight flight) {
        List<WeatherSnapshotResponse> snapshots = flight.getWeatherSnapshots().stream()
                .map(WeatherSnapshotResponse::from)
                .toList();

        return FlightResponse.builder()
                .id(flight.getId())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .durationInMinutes(flight.getDurationInMinutes())
                .aircraftId(flight.getAircraft().getId())
                .aircraftRegistration(flight.getAircraft().getRegistration())
                .aircraftType(flight.getAircraft().getType())
                .aircraftModel(flight.getAircraft().getModel())
                .passengers(flight.getPassengers())
                .landings(flight.getLandings())
                .pilotFunction(flight.getPilotFunction())
                .flightType(flight.getFlightType())
                .cost(flight.getCost())
                .remarks(flight.getRemarks())
                .createdAt(flight.getCreatedAt())
                .weatherSnapshots(snapshots)
                .originAirport(AirportResponse.from(flight.getOriginAirport()))
                .destinationAirport(AirportResponse.from(flight.getDestinationAirport()))
                .build();
    }
}