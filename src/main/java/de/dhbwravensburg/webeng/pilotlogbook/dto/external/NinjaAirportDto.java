package de.dhbwravensburg.webeng.pilotlogbook.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 1:1 mapping of a single entry in the response array of api-ninjas
 * {@code /v1/airports}. Snake-case fields are mapped with {@link JsonProperty};
 * unknown fields are ignored so future additions on the upstream side do not
 * break deserialisation.
 * <p>
 * This DTO is internal — never serialise it back through the REST API. Map to
 * {@link de.dhbwravensburg.webeng.pilotlogbook.model.Airport} and return
 * {@link de.dhbwravensburg.webeng.pilotlogbook.dto.response.AirportResponse}.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NinjaAirportDto {

    private String icao;
    private String iata;
    private String name;
    private String city;
    private String region;
    private String country;

    @JsonProperty("elevation_ft")
    private Integer elevationFt;

    private Double latitude;
    private Double longitude;
    private String timezone;

    /** enum {@code "small" | "medium" | "large"} */
    private String size;

    @JsonProperty("num_runways")
    private Integer numRunways;

    private List<RunwayDto> runways;

    /**
     * Nested runway entry
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RunwayDto {

        /** Runway length in feet. */
        private Integer length;

        /** Runway width in feet. */
        private Integer width;

        @JsonProperty("has_lights")
        private Boolean hasLights;
    }
}