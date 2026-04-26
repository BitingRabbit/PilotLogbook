package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

/**
 * Request body for updating an existing flight log entry.
 * 
 * <p>All fields are optional. Only non-{@code null} fields are updated
 * so a single field can be changed without sending the full object.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateFlightRequest {

    /** New 4-letter ICAO code for the departure airport */
    @Size(min = 4, max = 4, message = "Departure ICAO must be exactly 4 characters")
    private String departureIcao;

    /** New 4-letter ICAO code for the destination airport */
    @Size(min = 4, max = 4, message = "Destination ICAO must be exactly 4 characters")
    private String destinationIcao;

    /** New departure date and time */
    private LocalDateTime departureTime;

    /** New arrival date and time */
    private LocalDateTime arrivalTime;

    /** ID of the replacement aircraft */
    private Long aircraftId;

    /** Updated passenger count (0 or more) */
    @Min(value = 0, message = "Passengers cannot be negative")
    private Integer passengers;

    /** Updated number of landings (at least 1) */
    @Min(value = 1, message = "At least 1 landing required")
    private Integer landings;

    /** Updated pilot function for this flight */
    private PilotFunction pilotFunction;

    /** Updated flight rules category (VFR or IFR) */
    private FlightType flightType;

    /** Updated flight cost (0.0 or more) */
    @DecimalMin(value = "0.0", message = "Cost cannot be negative")
    private BigDecimal cost;

    /** Updated free-text remarks, at most 500 characters */
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}