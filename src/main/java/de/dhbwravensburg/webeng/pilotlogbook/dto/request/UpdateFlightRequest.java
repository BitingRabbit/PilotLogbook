package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request body for updating an existing flight log entry.
 *
 * <p>All fields are optional. Only non-{@code null} fields are updated
 * so a single field can be changed without sending the full object.
 *
 * @param originIcao      new 4-letter ICAO code for the departure airport
 * @param destinationIcao new 4-letter ICAO code for the destination airport
 * @param departureTime   new departure date and time
 * @param arrivalTime     new arrival date and time
 * @param aircraftId      ID of the replacement aircraft
 * @param passengers      updated passenger count (0 or more)
 * @param landings        updated number of landings (at least 1)
 * @param pilotFunction   updated pilot function for this flight
 * @param flightType      updated flight rules category (VFR or IFR)
 * @param cost            updated flight cost (0.0 or more)
 * @param remarks         updated free-text remarks, at most 500 characters
 */
public record UpdateFlightRequest(

        @Size(min = 4, max = 4, message = "Departure ICAO must be exactly 4 characters")
        String originIcao,

        @Size(min = 4, max = 4, message = "Destination ICAO must be exactly 4 characters")
        String destinationIcao,

        LocalDateTime departureTime,

        LocalDateTime arrivalTime,

        Long aircraftId,

        @Min(value = 0, message = "Passengers cannot be negative")
        Integer passengers,

        @Min(value = 1, message = "At least 1 landing required")
        Integer landings,

        PilotFunction pilotFunction,

        FlightType flightType,

        @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal cost,

        @Size(max = 500, message = "Remarks must not exceed 500 characters")
        String remarks
) {}