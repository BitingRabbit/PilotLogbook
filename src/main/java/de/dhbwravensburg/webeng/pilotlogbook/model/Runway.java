package de.dhbwravensburg.webeng.pilotlogbook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Describes a single runway of an {@link Airport}.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
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