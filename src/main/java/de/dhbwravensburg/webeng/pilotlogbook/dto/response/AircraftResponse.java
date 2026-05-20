package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft.EngineType;

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
public record AircraftResponse(
        Long id,
        String registration,
        String type,
        String model,
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