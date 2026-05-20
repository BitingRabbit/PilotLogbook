package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import de.dhbwravensburg.webeng.pilotlogbook.model.Airport;
import de.dhbwravensburg.webeng.pilotlogbook.model.Runway;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Public API response for airport master data, mapped from the cached
 * {@link Airport} entity. Internal fields like {@code fetchedAt} are
 * deliberately not exposed — they are an implementation detail of the cache.
 *
 * <p>Use {@link #from(Airport)} to map.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AirportResponse {

    /** ICAO code (4 letters, uppercase). */
    private String icao;

    /** IATA code (3 letters, uppercase). May be {@code null} for small airfields. */
    private String iata;

    /** Full airport name. */
    private String name;

    /** City the airport is associated with, if known. */
    private String city;

    /** ISO 3166-1 alpha-2 country code, e.g. {@code "DE"}. */
    private String country;

    /** Field elevation in feet above mean sea level. */
    private Integer elevationInFt;

    /** Latitude in decimal degrees (WGS-84). */
    private Double latitude;

    /** Longitude in decimal degrees (WGS-84). */
    private Double longitude;

    /** IANA timezone identifier, e.g. {@code "Europe/Berlin"}. */
    private String timezone;

    /** Coarse size classification. */
    private Airport.Size size;

    /** Runways available at the airport. */
    private List<RunwayResponse> runways;

    public static AirportResponse from(Airport airport) {
        List<RunwayResponse> runways = airport.getRunways().stream()
                .map(RunwayResponse::from)
                .toList();

        return AirportResponse.builder()
                .icao(airport.getIcao())
                .iata(airport.getIata())
                .name(airport.getName())
                .city(airport.getCity())
                .country(airport.getCountry())
                .elevationInFt(airport.getElevationInFt())
                .latitude(airport.getLatitude())
                .longitude(airport.getLongitude())
                .timezone(airport.getTimezone())
                .size(airport.getSize())
                .runways(runways)
                .build();
    }

    /**
     * Public API representation of a single runway. Nested because runways are
     * only ever exposed as part of an airport.
     *
     * @param lengthInFt runway length in feet
     * @param widthInFt  runway width in feet
     * @param hasLights  {@code true} if the runway is lit, {@code null} if unknown
     */
    public record RunwayResponse(
            Integer lengthInFt,
            Integer widthInFt,
            Boolean hasLights
    ) {
        public static RunwayResponse from(Runway runway) {
            return new RunwayResponse(
                    runway.getLengthInFt(),
                    runway.getWidthInFt(),
                    runway.getHasLights()
            );
        }
    }
}