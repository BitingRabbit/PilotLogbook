package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import de.dhbwravensburg.webeng.pilotlogbook.model.Airport;
import de.dhbwravensburg.webeng.pilotlogbook.model.Runway;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Public API response for airport data, mapped from the cached
 * {@link Airport} entity.
 *
 * <p>Use {@link #from(Airport)} to map.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Airport master data,, ICAO/IATA, location, elevation, runways")
public class AirportResponse {

    /** ICAO code (4 letters, uppercase). */
    @Schema(description = "ICAO code (4 letters, uppercase)", example = "EDDS")
    private String icao;

    /** IATA code (3 letters, uppercase). May be {@code null} for small airfields. */
    @Schema(description = "IATA code (3 letters). Null for small airfields.", example = "STR")
    private String iata;

    /** Full airport name. */
    @Schema(description = "Full airport name", example = "Stuttgart Airport")
    private String name;

    /** City the airport is associated with, if known. */
    @Schema(description = "Associated city", example = "Stuttgart")
    private String city;

    /** 2 letter country code, e.g. {@code "DE"}. */
    @Schema(description = "2 letter country code", example = "DE")
    private String country;

    /** Field elevation in feet above mean sea level. */
    @Schema(description = "Field elevation in feet AMSL", example = "1276")
    private Integer elevationInFt;

    /** Latitude in decimal degrees. */
    @Schema(description = "Latitude in decimal degrees", example = "48.6899")
    private Double latitude;

    /** Longitude in decimal degrees. */
    @Schema(description = "Longitude in decimal degrees", example = "9.2220")
    private Double longitude;

    /** timezone identifier, e.g. {@code "Europe/Berlin"}. */
    @Schema(description = "timezone identifier", example = "Europe/Berlin")
    private String timezone;

    /** Airport size classification. */
    @Schema(description = "Airport size classification")
    private Airport.Size size;

    /** Runways available at the airport. */
    @Schema(description = "Runways existing at the airport")
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
     * Public API representation of a single runway. Nested.
     *
     * @param lengthInFt runway length in feet
     * @param widthInFt  runway width in feet
     * @param hasLights  {@code true} if the runway is lit, {@code null} if unknown
     */
    @Schema(description = "A single runway at an airport")
    public record RunwayResponse(
            @Schema(description = "Runway length in feet", example = "10827")
            Integer lengthInFt,

            @Schema(description = "Runway width in feet", example = "148")
            Integer widthInFt,

            @Schema(description = "True if the runway is lit, null if unknown", example = "true")
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