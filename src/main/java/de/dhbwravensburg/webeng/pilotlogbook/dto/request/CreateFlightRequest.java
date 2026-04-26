package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for creating a new flight log entry.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateFlightRequest {

    /** 4-letter ICAO code of the departure airport */
    @NotBlank(message = "Departure airport empty!")
    @Size(min = 4, max = 4, message = "Departure airport needs to be exactly 4 characters")
    private String departureIcao;

    /** 4-letter ICAO code of the destination airport */
    @NotBlank(message = "Destination airport empty!")
    @Size(min = 4, max = 4, message = "Destination airport needs to be exactly 4 characters")
    private String destinationIcao;

    /** Date and time of departure */
    @NotNull(message = "Departure time empty!")
    private LocalDateTime departureTime;

    /** Date and time of arrival */
    @NotNull(message = "Arrival time empty!")
    private LocalDateTime arrivalTime;

    /** ID of the aircraft used for this flight */
    @NotNull(message = "Aircraft is required")
    private Long aircraftId;

    /** Number of passengers on board (0 or more) */
    @Min(value = 0, message = "Passengers cannot be negative")
    private Integer passengers;

    /** Number of landings performed (at least 1) */
    @NotNull(message = "Landings empty!")
    @Min(value = 1, message ="At least 1 landing required")
    private Integer landings;

    /** Role of the pilot during this flight */
    @NotNull(message = "Pilot function empty!")
    private PilotFunction pilotFunction;

    /** Flight rules category (VFR or IFR) */
    @NotNull(message = "Flight Type empty!")
    private FlightType flightType;

    /** Optional cost associated with this flight (0.0 or more) */
    @DecimalMin(value = "0.0", message = "Cost cannot be negative")
    private BigDecimal cost;

    /** Optional free-text remarks, at most 500 characters */
    @Size(max = 500, message = "Remarks must not exceed 500 characters!")
    private String remarks;
}