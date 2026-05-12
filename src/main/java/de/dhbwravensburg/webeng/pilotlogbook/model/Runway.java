package de.dhbwravensburg.webeng.pilotlogbook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Embeddable value object describing a single runway of an {@link Airport}.
 * Immutable by design — runway properties are populated once when the parent
 * airport is fetched from the external data source.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Runway {

    /** Runway length in feet */
    private Integer lengthInFt;

    /** Runway width in feet */
    private Integer widthInFt;

    /** Whether the runway is equipped with lights ({@code null} if unknown) */
    @Column(name = "has_lights")
    private Boolean hasLights;
}