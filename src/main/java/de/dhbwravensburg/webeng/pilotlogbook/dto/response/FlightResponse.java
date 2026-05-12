package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;
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
public class FlightResponse {
    
    private Long id;

    /** Date and time of departure. */
    private LocalDateTime departureTime;

    /** Date and time of arrival. */
    private LocalDateTime arrivalTime;

    /** Flight duration in minutes, derived from departure and arrival time. */
    private Long durationInMinutes;

    /** ID of the aircraft used for this flight. */
    private Long aircraftId;

    /** Registration number of the aircraft (e.g. D-ABCD). */
    private String aircraftRegistration;

    /** Category or type designator of the aircraft. */
    private String aircraftType;

    /** Specific model name of the aircraft. */
    private String aircraftModel;

    /** Number of passengers on board. */
    private Integer passengers;

    /** Number of landings performed. */
    private Integer landings;

    /** Role of the pilot during this flight. */
    private PilotFunction pilotFunction;

    /** Flight rules category (VFR or IFR). */
    private FlightType flightType;

    /** Cost associated with this flight, if recorded. */
    private BigDecimal cost;

    /** Free-text remarks entered by the pilot. */
    private String remarks;

    /** Timestamp when this flight entry was created. */
    private LocalDateTime createdAt;

    /** Weather snapshots captured at departure and arrival. Empty if not yet fetched. */
    private List<WeatherSnapshotResponse> weatherSnapshots;

    private AirportResponse originAirport;

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