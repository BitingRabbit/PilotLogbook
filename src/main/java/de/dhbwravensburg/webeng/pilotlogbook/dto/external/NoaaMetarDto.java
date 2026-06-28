package de.dhbwravensburg.webeng.pilotlogbook.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

/**
 * 1:1 mapping of a single METAR entry from the NOAA Aviation Weather Center API
 * ({@code GET /api/data/metar?ids=ICAO&format=json}).
 *
 * <p>Unknown fields from the upstream response are silently ignored
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoaaMetarDto {

    /** 4-letter ICAO airport code of the reporting station (e.g. {@code EDDS}). */
    private String icao;

    /** Observation time as a Unix epoch timestamp in seconds (UTC). */
    private Long obsTime;

    /** Air temperature in degrees Celsius. */
    private Double temp;

    /** Dew-point temperature in degrees Celsius. */
    private Double dewp;

    /**
     * Wind direction in degrees true (0–360).
     * {@code null} when wind is variable ({@code VRB}).
     */
    private Integer wdir;

    /** Mean wind speed in knots. */
    private Integer wspd;

    /** Gust speed in knots. {@code null} when no gusts are reported. */
    private Integer wgst;

    /**
     * Prevailing visibility as a string (e.g. {@code "6+"}, {@code "10"}).
     */
    private String visib;

    /** Altimeter setting. */
    private Double altim;

    /**
     * weather phenomenon codes
     * (e.g. {@code "-RA BR"} for light rain and mist). {@code null} or blank when none reported.
     */
    private String wxString;

    /** raw METAR string as issued by the station (e.g. {@code EDDS 121220Z 27008KT ...}). */
    private String rawOb;

    /**
     * flight category
     * {@code VFR}, {@code MVFR}, {@code IFR}, or {@code LIFR}.
     */
    private String fltCat;

    /** Sky cover code for the lowest layer. */
    private String cover;

    /** All reported cloud layers. */
    private List<CloudLayer> clouds;

    /**
     * A single cloud layer as reported in the METAR.
     */
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CloudLayer {

        /**
         * Sky coverage abbreviation: {@code FEW} (1–2 oktas), {@code SCT} (3–4),
         * {@code BKN} (5-7), {@code OVC} (8 / overcast).
         */
        private String cover;

        /** Cloud base height */
        private Integer base;
    }
}