package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Parsed METAR observation returned by the weather endpoints.
 *
 * <p>Contains both the raw METAR string and a fully decoded {@link DecodedMetar} breakdown so the
 * frontend can render individual weather parameters without parsing the raw string itself.
 *
 * @param icao            4-letter ICAO airport code the observation belongs to (e.g. {@code EDDS})
 * @param observationTime UTC time of the observation; {@code null} when deserialized from a stored snapshot
 * @param rawMetar        unmodified raw METAR string as delivered by NOAA
 * @param decodedMetar    structured breakdown of the raw METAR
 */
@Schema(description = "Parsed METAR observation including raw text and decoded fields")
public record MetarResponse(

        @Schema(description = "ICAO code of the observation", example = "EDDS")
        String icao,

        @Schema(description = "UTC time of the observation. Null when deserialized from a stored snapshot.",
                example = "2026-05-27T13:50:00")
        LocalDateTime observationTime,

        @Schema(description = "Raw METAR string as delivered by NOAA",
                example = "EDDS 271350Z 24008KT 9999 FEW040 22/12 Q1018 NOSIG")
        String rawMetar,

        @Schema(description = "Structured breakdown of the raw METAR")
        DecodedMetar decodedMetar
) {

    /**
     * All decoded weather parameters of a single METAR observation.
     */
    @Schema(description = "Decoded weather parameters of a single METAR observation")
    public record DecodedMetar(
            @Schema(description = "Surface wind conditions")
            Wind wind,

            @Schema(description = "Prevailing visibility")
            Visibility visibility,

            @Schema(description = "Air temperature and dew point")
            Temperature temperature,

            @Schema(description = "Altimeter setting / QNH")
            Pressure pressure,

            @Schema(description = "Cloud layers ordered from lowest to highest. Empty when CAVOK.")
            List<CloudLayer> clouds,

            @Schema(description = "Present weather phenomena codes (e.g. -RA, BR). Empty when none reported.",
                    example = "[\"-RA\", \"BR\"]")
            List<String> phenomena,

            @Schema(description = "Flight category derived from visibility and ceiling",
                    example = "VFR",
                    allowableValues = {"VFR", "MVFR", "IFR", "LIFR"})
            String flightCategory
    ) {}

    /**
     * Surface wind conditions.
     */
    @Schema(description = "Surface wind conditions")
    public record Wind(
            @Schema(description = "Wind direction in degrees true. Null when variable (VRB).", example = "240")
            Integer directionDeg,

            @Schema(description = "Mean wind speed in knots", example = "8")
            Integer speedKt,

            @Schema(description = "Gust speed in knots. Null when no gusts reported.", example = "15")
            Integer gustKt,

            @Schema(description = "True when the METAR contains VRB", example = "false")
            boolean variable
    ) {}

    /**
     * Prevailing visibility.
     */
    @Schema(description = "Prevailing visibility")
    public record Visibility(
            @Schema(description = "Visibility value as string", example = "9999")
            String value,

            @Schema(description = "True when METAR explicitly reports CAVOK", example = "false")
            boolean cavok
    ) {}

    /**
     * Air temperature and dew point.
     */
    @Schema(description = "Air temperature and dew point")
    public record Temperature(
            @Schema(description = "Outside air temperature in °C", example = "22.0")
            Double tempC,

            @Schema(description = "Dew-point temperature in °C", example = "12.0")
            Double dewpointC
    ) {}

    /**
     * Altimeter setting / QNH.
     */
    @Schema(description = "Altimeter setting / QNH")
    public record Pressure(
            @Schema(description = "QNH in hectopascal", example = "1018.0")
            Double qnhHpa
    ) {}

    /**
     * A single cloud layer reported in the METAR.
     */
    @Schema(description = "A single cloud layer reported in the METAR")
    public record CloudLayer(
            @Schema(description = "Sky coverage", example = "FEW",
                    allowableValues = {"FEW", "SCT", "BKN", "OVC"})
            String cover,

            @Schema(description = "Cloud base height in feet above aerodrome elevation", example = "4000")
            Integer baseFt
    ) {}
}