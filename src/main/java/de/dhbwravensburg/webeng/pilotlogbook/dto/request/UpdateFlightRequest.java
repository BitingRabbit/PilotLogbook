package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Partial update for a flight log entry, only non-null fields are applied")
public record UpdateFlightRequest(

        @Schema(description = "New ICAO code of the departure airport", example = "EDDS")
        @Size(min = 4, max = 4, message = "Departure ICAO must be exactly 4 characters")
        String originIcao,

        @Schema(description = "New ICAO code of the destination airport", example = "EDDM")
        @Size(min = 4, max = 4, message = "Destination ICAO must be exactly 4 characters")
        String destinationIcao,

        @Schema(description = "New departure date-time", example = "2026-05-27T10:30:00")
        LocalDateTime departureTime,

        @Schema(description = "New arrival date-time. Must remain after departure.", example = "2026-05-27T12:00:00")
        LocalDateTime arrivalTime,

        @Schema(description = "ID of the replacement aircraft", example = "2")
        Long aircraftId,

        @Schema(description = "Updated passenger count", example = "3")
        @Min(value = 0, message = "Passengers cannot be negative")
        Integer passengers,

        @Schema(description = "Updated number of landings", example = "2")
        @Min(value = 1, message = "At least 1 landing required")
        Integer landings,

        @Schema(description = "Updated pilot role")
        PilotFunction pilotFunction,

        @Schema(description = "Updated flight rules category")
        FlightType flightType,

        @Schema(description = "Updated flight cost in EUR", example = "150.00")
        @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal cost,

        @Schema(description = "Updated free-text remarks", example = "Smooth conditions, perfect landing")
        @Size(max = 500, message = "Remarks must not exceed 500 characters")
        String remarks
) {}