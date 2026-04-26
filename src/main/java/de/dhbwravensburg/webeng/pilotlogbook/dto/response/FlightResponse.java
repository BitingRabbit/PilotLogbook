package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response payload returned when reading aircraft data.
 * 
 * <p>Contains only fields exposed by the API.
 * Use {@link #from(Flight)} to create an instance from a JPA entity.
 * Aircraft details are inlined to avoid a separate lookup on the client side.
 */
@Getter
@AllArgsConstructor
public class FlightResponse {
    
    private Long id;

    /** ICAO code of the departure airport. */
    private String departureIcao;

    /** ICAO code of the destination airport. */
    private String destinationIcao;

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

    /**
     * Maps a {@link Flight} entity to this {@link FlightResponse} DTO.
     *
     * @param flight flight entity to map, must not be {@Code null}
     * @return new {@code FlightResponse} with mapped values
     */
    public static FlightResponse from(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getDepartureIcao(),
                flight.getDestinationIcao(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getDurationInMinutes(),
                flight.getAircraft().getId(),
                flight.getAircraft().getRegistration(),
                flight.getAircraft().getType(),
                flight.getAircraft().getModel(),
                flight.getPassengers(),
                flight.getLandings(),
                flight.getPilotFunction(),
                flight.getFlightType(),
                flight.getCost(),
                flight.getRemarks(),
                flight.getCreatedAt()
        );
    }
}