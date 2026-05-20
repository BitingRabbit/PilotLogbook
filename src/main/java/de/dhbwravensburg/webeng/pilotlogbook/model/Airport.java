package de.dhbwravensburg.webeng.pilotlogbook.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing an airport.
 * The ICAO code is used as the unique identifier for airports
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "airports", indexes = @Index(name = "idx_airport_icao", columnList = "icao"))
public class Airport {

    public enum Size { SMALL, MEDIUM, LARGE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /** ICAO code of the airport (exactly 4 characters, uppercase). */
    @EqualsAndHashCode.Include
    @Column(nullable = false, length = 4, unique = true)
    @Setter(AccessLevel.NONE)
    private String icao;

    /** IATA code (3 letters, uppercase). May be {@code null} for small airfields. */
    @Column(length = 3, unique = true)
    @Setter(AccessLevel.NONE)
    private String iata;

    /** Full airport name as returned by the data source. */
    @Column(nullable = false)
    private String name;

    private String city;

    private String country;

    private Integer elevationInFt;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String timezone;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Setter
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "airport_runways", joinColumns = @JoinColumn(name = "airport_id"))
    private Set<Runway> runways = new HashSet<>();

    /** Timestamp of the last successful fetch from the external API. */
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime fetchedAt;

    /**
     * Creates a new Airport. The ICAO is normalized to uppercase, and {@code fetchedAt}
     * is initialized to the current time — callers do not need to set it manually.
     */
    @Builder
    public Airport(String icao,
                   String iata,
                   String name,
                   String city,
                   String country,
                   Integer elevationInFt,
                   Double latitude,
                   Double longitude,
                   String timezone,
                   Size size,
                   Set<Runway> runways) {
        this.icao = icao.toUpperCase();
        this.iata = (iata == null || iata.isBlank()) ? null : iata.toUpperCase();
        this.name = name;
        this.city = city;
        this.country = country;
        this.elevationInFt = elevationInFt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.size = size;
        if (runways != null) this.runways = runways;
        this.fetchedAt = LocalDateTime.now();
    }
}