package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft.EngineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating a new aircraft.
 *
 * <p>All fields except {@code model} are required. {@code registration} must
 * match the pattern {@code ^[A-Z0-9-]+$} (uppercase ICAO style).
 *
 * @param registration ICAO/national registration mark (e.g. {@code D-ABCD}),
 *                     4-6 uppercase alphanumeric characters or hyphens
 * @param type         ICAO type designator (e.g. {@code C172}), 2–4 characters
 * @param model        optional free-text model/variant name, at most 50 characters
 * @param engineType   the engine/propulsion type
 */
@Schema(description = "Payload for creating a new aircraft in the authenticated pilot's fleet")
public record CreateAircraftRequest(

        @Schema(
                description = "Registration mark: 4–6 chars, uppercase letters, digits or hyphens",
                example = "D-ABCD"
        )
        @NotBlank(message = "Registration empty")
        @Size(min = 4, max = 6, message = "Registration must be between 4 and 6 characters")
        @Pattern(regexp = "^[A-Z0-9-]+$", message = "Registration must contain only uppercase letters, digits, or hyphens")
        String registration,

        @Schema(description = "ICAO type designator (2–4 chars)", example = "C172")
        @NotBlank(message = "Type must not be empty")
        @Size(min = 2, max = 4, message = "Type must be between 2 and 4 characters")
        String type,

        @Schema(description = "Free-text model or variant name", example = "Skyhawk")
        @Size(max = 50, message = "Model must not exceed 50 characters")
        String model,

        @Schema(description = "Engine / propulsion type")
        @NotNull(message = "Engine type must not be empty")
        EngineType engineType
) {}