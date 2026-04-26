package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft.EngineType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for updating an existing aircraft.
 *
 * <p>All fields are optional. Only non-{@code null} fields are updated
 * so a single field can be changed without sending the full object.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateAircraftRequest {

    /**
     * New registration mark.
     * When provided, must be 4–6 uppercase alphanumeric characters or hyphens
     */
    @Size(min = 4, max = 6, message = "Registration must be between 4 and 6 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Registration must contain only uppercase letters, digits, or hyphens")
    private String registration;

    /**
     * New ICAO type designator
     * When provided, must be 2–4 characters.
     */
    @Size(min = 2, max = 4, message = "Type must be between 2 and 4 characters")
    private String type;

    /** New model/variant name
     * When provided, must not exceed 50 characters */
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    /** New engine/propulsion type
     * Must be a valid {@link EngineType} value when provided */
    private EngineType engineType;
}