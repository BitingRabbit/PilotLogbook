package de.dhbwravensburg.webeng.pilotlogbook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a single flight entry in the pilot's logbook.
 * Duration is derived automatically from departure and arrival time.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "flights")
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    /** The pilot who logged this flight */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pilot_id", nullable = false)
    @Setter(AccessLevel.NONE)
    private Pilot pilot;

    /** Date and time of departure */
    @Column(nullable = false)
    private LocalDateTime departureTime;

    /** Date and time of arrival */
    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    /** Flight duration in minutes, derived from departure and arrival time */
    @Column(nullable = false)
    @Positive
    private Long durationInMinutes;

    /** The aircraft used for this flight */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    /** Number of passengers on board, zero or more */
    @PositiveOrZero
    private Integer passengers;

    /** Number of landings performed during this flight */
    @Column(nullable = false)
    @Positive
    private Integer landings;

    /**
     * Role the pilot fulfilled during this flight.
     * <ul>
     *   <li>PIC – Pilot in Command</li>
     *   <li>SIC – Second in Command</li>
     *   <li>DUAL – Under instruction</li>
     *   <li>INSTRUCTOR – Giving instruction</li>
     * </ul>
     */
    public enum PilotFunction {
        PIC, SIC, DUAL, INSTRUCTOR
    }

    /** Pilot's function during this flight */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PilotFunction pilotFunction;

    /**
     * Flight rules under which the flight was conducted
     * <ul>
     *   <li>VFR – Visual Flight Rules</li>
     *   <li>IFR – Instrument Flight Rules</li>
     * </ul>
     */
    public enum FlightType {
        VFR, IFR
    }

    /** Flight rules category for this flight */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private FlightType flightType;

    /** Optional cost associated with this flight */
    @Column(precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal cost;

    /** Free-text remarks, at most 500 characters */
    @Column(length = 500)
    private String remarks;

    /**
     * Weather snapshots captured for this flight (typically one for departure and
     * one for arrival). The list is owned by the flight: snapshots are persisted
     * and removed together with the flight.
     */
    @OneToMany(mappedBy = "flight",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Setter(AccessLevel.NONE)
    private List<WeatherSnapshot> weatherSnapshots = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origin_airport_id", nullable = false)
    private Airport originAirport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_airport_id", nullable = false)
    private Airport destinationAirport;

    /** Timestamp set once on first persist; never updated afterward */
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a fully initialised Flight and validates that arrival is after departure.
     *
     * @param pilot           the logging pilot
     * @param departureTime   date/time of departure
     * @param arrivalTime     date/time of arrival (must be after {@code departureTime})
     * @param aircraft        aircraft used for the flight
     * @param passengers      number of passengers (0 or more)
     * @param landings        number of landings performed (at least 1)
     * @param pilotFunction   role of the pilot during the flight
     * @param flightType      VFR or IFR
     * @param cost            optional flight cost
     * @param remarks         optional free-text remarks
     * @throws IllegalArgumentException if {@code arrivalTime} is not after {@code departureTime}
     */
    @Builder
    public Flight(Pilot pilot,
                  LocalDateTime departureTime,
                  LocalDateTime arrivalTime,
                  Aircraft aircraft,
                  Integer passengers,
                  Integer landings,
                  PilotFunction pilotFunction,
                  FlightType flightType,
                  BigDecimal cost,
                  Airport originAirport,
                  Airport destinationAirport,
                  String remarks) {
        this.pilot = pilot;
        if (!arrivalTime.isAfter(departureTime))
            throw new IllegalArgumentException("Arrival time must be after departure time");
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationInMinutes = Duration.between(departureTime, arrivalTime).toMinutes();
        this.aircraft = aircraft;
        this.passengers = passengers;
        this.landings = landings;
        this.pilotFunction = pilotFunction;
        this.flightType = flightType;
        this.cost = cost;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.remarks = remarks;
    }

    /**
     * Sets {@link #createdAt} to the current time before the entity is first persisted
     */
    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Runs before each update: checks arrival is after departure,
     * then recomputes {@link #durationInMinutes}.
     *
     * @throws IllegalArgumentException if {@code arrivalTime} is not after {@code departureTime}
     */
    @PreUpdate
    private void onUpdate() {
        if (!arrivalTime.isAfter(departureTime))
            throw new IllegalArgumentException("Arrival time must be after departure time");
        this.durationInMinutes = Duration.between(departureTime, arrivalTime).toMinutes();
    }
}