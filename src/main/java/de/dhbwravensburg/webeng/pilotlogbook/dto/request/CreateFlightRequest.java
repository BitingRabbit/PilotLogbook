package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request body for creating a new flight log entry.
 *
 * @param originIcao      4-letter ICAO code of the departure airport
 * @param destinationIcao 4-letter ICAO code of the destination airport
 * @param departureTime   date and time of departure
 * @param arrivalTime     date and time of arrival
 * @param aircraftId      ID of the aircraft used for this flight
 * @param passengers      number of passengers on board (0 or more)
 * @param landings        number of landings performed (at least 1)
 * @param pilotFunction   role of the pilot during this flight
 * @param flightType      flight rules category (VFR or IFR)
 * @param cost            optional cost associated with this flight (0.0 or more)
 * @param remarks         optional free-text remarks, at most 500 characters
 */
@Schema(description = "Payload for creating a new flight log entry")
public record CreateFlightRequest(

        @Schema(description = "ICAO code of the departure airport", example = "EDDM")
        @NotBlank(message = "Departure airport empty!")
        @Size(min = 4, max = 4, message = "Departure airport needs to be exactly 4 characters")
        String originIcao,

        @Schema(description = "ICAO code of the destination airport", example = "EDDF")
        @NotBlank(message = "Destination airport empty!")
        @Size(min = 4, max = 4, message = "Destination airport needs to be exactly 4 characters")
        String destinationIcao,

        @Schema(description = "Departure date-time (ISO-8601, local time)", example = "2026-05-27T10:30:00")
        @NotNull(message = "Departure time empty!")
        LocalDateTime departureTime,

        @Schema(description = "Arrival date-time (ISO-8601, local time). Must be after departure.", example = "2026-05-27T12:00:00")
        @NotNull(message = "Arrival time empty!")
        LocalDateTime arrivalTime,

        @Schema(description = "ID of the aircraft used for this flight", example = "42")
        @NotNull(message = "Aircraft is required")
        Long aircraftId,

        @Schema(description = "Number of passengers on board", example = "2")
        @Min(value = 0, message = "Passengers cannot be negative")
        Integer passengers,

        @Schema(description = "Number of landings performed (at least 1)", example = "1")
        @NotNull(message = "Landings empty!")
        @Min(value = 1, message = "At least 1 landing required")
        Integer landings,

        @Schema(description = "Pilot role during this flight (PIC, COPILOT, INSTRUCTOR, STUDENT)")
        @NotNull(message = "Pilot function empty!")
        PilotFunction pilotFunction,

        @Schema(description = "Flight rules category (VFR or IFR)")
        @NotNull(message = "Flight Type empty!")
        FlightType flightType,

        @Schema(description = "Optional flight cost in EUR", example = "120.50")
        @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal cost,

        @Schema(description = "Free-text remarks (max 500 chars)", example = "Crosswind training, gusty conditions")
        @Size(max = 500, message = "Remarks must not exceed 500 characters!")
        String remarks
) {}