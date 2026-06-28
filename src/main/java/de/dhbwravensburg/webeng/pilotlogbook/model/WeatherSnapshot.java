package de.dhbwravensburg.webeng.pilotlogbook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A snapshot of the METAR weather for a specific {@link Flight}.
 * <p>
 * These are created as {@link Status#PENDING} when the flight is saved.
 * A background worker then attempts to fetch the weather, updating the status to
 * {@link Status#AVAILABLE} (with data) or {@link Status#UNAVAILABLE} (if the API fails).
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "weather_snapshots")
public class WeatherSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    /** The flight this snapshot belongs to. Loaded lazily to avoid N+1 queries */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    /** Phase of the flight this snapshot was captured for */
    public enum PhaseType {
        /** METAR for the departure airport at the departure time */
        DEPARTURE,
        /** METAR for the destination airport at the arrival time */
        ARRIVAL
    }

    /** Whether the snapshot describes departure or arrival weather */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PhaseType phaseType;

    /** ICAO code of the observed airport (exactly 4 characters) */
    @Column(nullable = false, length = 4)
    @Size(min = 4, max = 4)
    private String icao;

    /**
     * Raw METAR string as returned by the upstream service.
     * {@code null} unless {@link #status} is {@link Status#AVAILABLE}.
     */
    @Column(length = 500)
    private String rawMetar;

    /**
     * Decoded METAR fields serialised as JSON.
     * {@code null} unless {@link #status} is {@link Status#AVAILABLE}.
     */
    @Column(columnDefinition = "TEXT")
    private String decodedMetarJson;

    /** Lifecycle of an asynchronously fetched snapshot */
    public enum Status {
        /** Row persisted, upstream call not finished yet */
        PENDING,
        /** Upstream returned data; {@link #rawMetar} and {@link #decodedMetarJson} are populated */
        AVAILABLE,
        /** Upstream call failed or returned no data for the requested time */
        UNAVAILABLE
    }

    /** Current processing status of this snapshot */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Status status;

    /** Timestamp set once on first persist. Never updated afterward */
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new snapshot in {@link Status#PENDING} state. The actual METAR
     * data is filled in later via {@link #markAvailable(String, String)},
     * or via {@link #markUnavailable()} (if fetching fails).
     *
     * @param phaseType     departure or arrival
     * @param icao          4-letter ICAO code of the observed airport
     */
    public WeatherSnapshot(PhaseType phaseType, String icao) {
        this.phaseType = phaseType;
        this.icao = icao;
        this.status = Status.PENDING;
    }

    /**
     * Marks the snapshot as {@link Status#AVAILABLE} and stores the fetched data.
     *
     * @param rawMetar             raw METAR string
     * @param decodedMetarJson     decoded METAR fields as JSON
     */
    public void markAvailable(String rawMetar, String decodedMetarJson) {

        this.rawMetar = rawMetar;
        this.decodedMetarJson = decodedMetarJson;
        this.status = Status.AVAILABLE;
    }

    /** Marks this snapshot as permanently {@link Status#UNAVAILABLE} */
    public void markUnavailable() {
        this.status = Status.UNAVAILABLE;
    }

    void setFlight(Flight flight) {
        this.flight = flight;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}