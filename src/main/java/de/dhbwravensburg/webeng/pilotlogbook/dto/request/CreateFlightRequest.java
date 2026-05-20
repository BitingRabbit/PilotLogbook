package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.FlightType;
import de.dhbwravensburg.webeng.pilotlogbook.model.Flight.PilotFunction;
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
public record CreateFlightRequest(

        @NotBlank(message = "Departure airport empty!")
        @Size(min = 4, max = 4, message = "Departure airport needs to be exactly 4 characters")
        String originIcao,

        @NotBlank(message = "Destination airport empty!")
        @Size(min = 4, max = 4, message = "Destination airport needs to be exactly 4 characters")
        String destinationIcao,

        @NotNull(message = "Departure time empty!")
        LocalDateTime departureTime,

        @NotNull(message = "Arrival time empty!")
        LocalDateTime arrivalTime,

        @NotNull(message = "Aircraft is required")
        Long aircraftId,

        @Min(value = 0, message = "Passengers cannot be negative")
        Integer passengers,

        @NotNull(message = "Landings empty!")
        @Min(value = 1, message = "At least 1 landing required")
        Integer landings,

        @NotNull(message = "Pilot function empty!")
        PilotFunction pilotFunction,

        @NotNull(message = "Flight Type empty!")
        FlightType flightType,

        @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal cost,

        @Size(max = 500, message = "Remarks must not exceed 500 characters!")
        String remarks
) {}