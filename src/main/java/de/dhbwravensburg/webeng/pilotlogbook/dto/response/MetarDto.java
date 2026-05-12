package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Parsed METAR observation returned by the weather endpoints.
 *
 * <p>Contains both the raw METAR string and a fully decoded {@link DecodedMetar} breakdown so the
 * frontend can render individual weather parameters without parsing the raw string itself.
 */
@Getter
@AllArgsConstructor
public class MetarDto {

    /** 4-letter ICAO airport code the observation belongs to (e.g. {@code EDDS}). */
    private String icao;

    /** UTC time of the observation. {@code null} when deserialized from a stored snapshot. */
    private LocalDateTime observationTime;

    /** Unmodified raw METAR string as delivered by NOAA (e.g. {@code EDDS 121220Z 27008KT ...}). */
    private String rawMetar;

    /** Structured breakdown of the raw METAR. */
    private DecodedMetar decodedMetar;

    /**
     * All decoded weather parameters of a single METAR observation.
     */
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DecodedMetar {
        private Wind wind;
        private Visibility visibility;
        private Temperature temperature;
        private Pressure pressure;

        /** Cloud layers ordered from lowest to highest base altitude. Empty list when CAVOK. */
        private List<CloudLayer> clouds;

        /**
         * Present-weather phenomena codes (e.g. {@code "-RA"}, {@code "BR"}).
         * Empty list when no significant weather is reported.
         */
        private List<String> phenomena;

        /**
         * FAA/ICAO flight category derived from visibility and ceiling:
         * {@code VFR}, {@code MVFR}, {@code IFR}, or {@code LIFR}.
         */
        private String flightCategory;
    }

    /**
     * Surface wind conditions.
     */
    @Getter
    @AllArgsConstructor
    public static class Wind {
        /** Wind direction in degrees true. {@code null} when wind is variable ({@code VRB}). */
        private Integer directionDeg;

        /** Mean wind speed in knots. */
        private Integer speedKt;

        /** Gust speed in knots. {@code null} when no gusts are reported. */
        private Integer gustKt;

        /** {@code true} when the METAR contains {@code VRB} (variable wind direction). */
        private boolean variable;
    }

    /**
     * Prevailing visibility.
     */
    @Getter
    @AllArgsConstructor
    public static class Visibility {
        /**
         * Visibility value as a string (e.g. {@code "6+"}, {@code "9999"}, {@code "0800"}).
         * The unit depends on the issuing station (statute miles in the US, metres in ICAO format).
         */
        private String value;

        /**
         * {@code true} when the METAR explicitly reports {@code CAVOK}
         * (Clouds and Visibility OK — no significant cloud below 5 000 ft, no CB, visibility ≥ 10 km).
         */
        private boolean cavok;
    }

    /**
     * Air temperature and dew point.
     */
    @Getter
    @AllArgsConstructor
    public static class Temperature {
        /** Outside air temperature in degrees Celsius. */
        private Double tempC;

        /** Dew-point temperature in degrees Celsius. */
        private Double dewpointC;
    }

    /**
     * Altimeter setting / QNH.
     */
    @Getter
    @AllArgsConstructor
    public static class Pressure {
        /** QNH in hectopascal (hPa / mbar), converted from the NOAA altimeter value. */
        private Double qnhHpa;
    }

    /**
     * A single cloud layer reported in the METAR.
     */
    @Getter
    @AllArgsConstructor
    public static class CloudLayer {
        /**
         * Sky coverage abbreviation: {@code FEW} (1–2 oktas), {@code SCT} (3–4),
         * {@code BKN} (5–7), {@code OVC} (8 / overcast).
         */
        private String cover;

        /** Cloud base height in feet above aerodrome elevation. */
        private Integer baseFt;
    }
}