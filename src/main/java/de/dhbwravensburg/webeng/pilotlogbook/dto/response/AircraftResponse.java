package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft.EngineType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response payload returned when reading aircraft data.
 *
 * <p>Contains only fields exposed by the API.
 * Use {@link #from(Aircraft)} to create an instance from a JPA entity.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AircraftResponse {

    private Long id;

    /** ICAO/national registration mark (e.g. {@code D-ABCD}) */
    private String registration;

    /** ICAO type designator (e.g. {@code C172}) */
    private String type;

    /** Optional free-text model/variant name */
    private String model;

    /** The engine/propulsion type of the aircraft */
    private EngineType engineType;

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
