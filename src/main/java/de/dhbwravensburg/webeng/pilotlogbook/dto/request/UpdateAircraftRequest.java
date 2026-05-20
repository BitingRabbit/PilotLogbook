package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft.EngineType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for updating an existing aircraft.
 *
 * <p>All fields are optional. Only non-{@code null} fields are updated
 * so a single field can be changed without sending the full object.
 *
 * @param registration new registration mark — 4–6 uppercase alphanumeric characters or hyphens
 * @param type         new ICAO type designator — 2–4 characters when provided
 * @param model        new model/variant name — at most 50 characters when provided
 * @param engineType   new engine/propulsion type
 */
public record UpdateAircraftRequest(

        @Size(min = 4, max = 6, message = "Registration must be between 4 and 6 characters")
        @Pattern(regexp = "^[A-Z0-9-]+$", message = "Registration must contain only uppercase letters, digits, or hyphens")
        String registration,

        @Size(min = 2, max = 4, message = "Type must be between 2 and 4 characters")
        String type,

        @Size(max = 50, message = "Model must not exceed 50 characters")
        String model,

        EngineType engineType
) {}