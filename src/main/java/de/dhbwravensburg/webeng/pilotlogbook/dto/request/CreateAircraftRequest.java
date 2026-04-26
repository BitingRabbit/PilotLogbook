package de.dhbwravensburg.webeng.pilotlogbook.dto.request;

import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft.EngineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for creating a new aircraft.
 *
 * <p>All fields except {@code model} are required. {@code registration} must
 * match the pattern {@code ^[A-Z0-9-]+$} (uppercase ICAO style).
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateAircraftRequest {

    /**
     * ICAO/national registration mark (e.g. {@code D-ABCD})
     * Must be 4–6 uppercase alphanumeric characters or hyphens
     */
    @NotBlank(message = "Registration empty")
    @Size(min = 4, max = 6, message = "Registration must be between 4 and 6 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Registration must contain only uppercase letters, digits, or hyphens")
    private String registration;

    /**
     * ICAO type designator (e.g. {@code C172})
     * Must be 2–4 characters
     */
    @NotBlank(message = "Type must not be empty")
    @Size(min = 2, max = 4, message = "Type must be between 2 and 4 characters")
    private String type;

    /** Optional free-text model/variant name, at most 50 characters */
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    /** The engine/propulsion type. Must be one of the {@link EngineType} enum values. */
    @NotNull(message = "Engine type must not be empty")
    private EngineType engineType;
}