package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft.EngineType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload returned when reading aircraft data.
 *
 * <p>Contains only fields exposed by the API.
 * Use {@link #from(Aircraft)} to create an instance from a JPA entity.
 *
 * @param id            database identifier
 * @param registration  ICAO/national registration mark (e.g. {@code D-ABCD})
 * @param type          ICAO type designator (e.g. {@code C172})
 * @param model         optional free-text model/variant name
 * @param engineType    the engine/propulsion type of the aircraft
 */
@Schema(description = "Aircraft as exposed by the API")
public record AircraftResponse(

        @Schema(description = "Database identifier", example = "1")
        Long id,

        @Schema(description = "Registration mark", example = "D-ABCD")
        String registration,

        @Schema(description = "ICAO type designator", example = "C172")
        String type,

        @Schema(description = "Model / variant name", example = "Skyhawk")
        String model,

        @Schema(description = "Engine / propulsion type")
        EngineType engineType
) {

    /**
     * Maps an {@link Aircraft} entity to this {@link AircraftResponse} DTO.
     *
     * @param aircraft aircraft entity to map, must not be {@code null}
     * @return new {@code AircraftResponse} with mapped values
     */
    public static AircraftResponse from(Aircraft aircraft) {
        return new AircraftResponse(
                aircraft.getId(),
                aircraft.getRegistration(),
                aircraft.getType(),
                aircraft.getModel(),
                aircraft.getEngineType()
        );
    }
}