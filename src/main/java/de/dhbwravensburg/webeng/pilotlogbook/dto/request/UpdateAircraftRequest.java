package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft.EngineType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Partial update for an aircraft, only non-null fields are applied")
public record UpdateAircraftRequest(

        @Schema(description = "New registration mark", example = "D-EFGH")
        @Size(min = 4, max = 6, message = "Registration must be between 4 and 6 characters")
        @Pattern(regexp = "^[A-Z0-9-]+$", message = "Registration must contain only uppercase letters, digits, or hyphens")
        String registration,

        @Schema(description = "New ICAO type designator", example = "PA28")
        @Size(min = 2, max = 4, message = "Type must be between 2 and 4 characters")
        String type,

        @Schema(description = "New model/variant name", example = "Cessna")
        @Size(max = 50, message = "Model must not exceed 50 characters")
        String model,

        @Schema(description = "New engine / propulsion type")
        EngineType engineType
) {}