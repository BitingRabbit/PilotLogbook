package de.dhbwravensburg.webeng.pilotlogbook.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing an aircraft owned by a pilot.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "aircrafts")
public class Aircraft {

    /** Classifies the propulsion system of the aircraft */
    public enum EngineType {
        /** Single-engine piston aircraft */
        SINGLE_PISTON,
        /** Multi-engine piston aircraft */
        MULTI_PISTON,
        /** Turboprop aircraft */
        TURBOPROP,
        /** Jet-powered aircraft */
        JET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    /** The pilot who owns this aircraft. Immutable after creation to prevent cross-tenant reassignment. */
    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pilot_id", nullable = false)
    private Pilot pilot;

    /**
     * ICAO/national registration mark (e.g. {@code D-EABC})
     */
    @Column(nullable = false, length = 10)
    private String registration;

    /**
     * ICAO type designator (e.g. {@code C172})
     * 2–4 characters
     */
    @Column(nullable = false, length = 4)
    private String type;

    /** Optional free-text model/variant name (e.g. {@code Cessna 172 Skyhawk}) */
    @Column(length = 50)
    private String model;

    /** The engine/propulsion type of this aircraft. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EngineType engineType;

    /** Timestamp set once on first persist. Never updated afterward. */
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new aircraft with all required fields.
     *
     * @param pilot        the owning pilot
     * @param registration the registration mark
     * @param type         the ICAO type designator
     * @param model        optional model/variant name, may be {@code null}
     * @param engineType   the engine/propulsion type
     */
    @Builder
    public Aircraft(Pilot pilot,
                    String registration,
                    String type,
                    String model,
                    EngineType engineType) {
        this.pilot = pilot;
        this.registration = registration;
        this.type = type;
        this.model = model;
        this.engineType = engineType;
    }

    /** Sets {@link #createdAt} to the current time before the first database insert */
    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}