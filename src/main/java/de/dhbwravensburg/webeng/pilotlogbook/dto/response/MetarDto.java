package de.dhbwravensburg.webeng.pilotlogbook.dto.response;

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
public record MetarDto(
        String icao,
        LocalDateTime observationTime,
        String rawMetar,
        DecodedMetar decodedMetar
) {

    /**
     * All decoded weather parameters of a single METAR observation.
     *
     * @param wind           surface wind conditions
     * @param visibility     prevailing visibility
     * @param temperature    air temperature and dew point
     * @param pressure       altimeter setting / QNH
     * @param clouds         cloud layers ordered from lowest to highest base altitude; empty when CAVOK
     * @param phenomena      present-weather phenomena codes (e.g. {@code "-RA"}, {@code "BR"}); empty when none reported
     * @param flightCategory FAA/ICAO flight category derived from visibility and ceiling:
     *                       {@code VFR}, {@code MVFR}, {@code IFR}, or {@code LIFR}
     */
    public record DecodedMetar(
            Wind wind,
            Visibility visibility,
            Temperature temperature,
            Pressure pressure,
            List<CloudLayer> clouds,
            List<String> phenomena,
            String flightCategory
    ) {}

    /**
     * Surface wind conditions.
     *
     * @param directionDeg wind direction in degrees true; {@code null} when wind is variable ({@code VRB})
     * @param speedKt      mean wind speed in knots
     * @param gustKt       gust speed in knots; {@code null} when no gusts are reported
     * @param variable     {@code true} when the METAR contains {@code VRB} (variable wind direction)
     */
    public record Wind(
            Integer directionDeg,
            Integer speedKt,
            Integer gustKt,
            boolean variable
    ) {}

    /**
     * Prevailing visibility.
     *
     * @param value visibility value as a string (e.g. {@code "6+"}, {@code "9999"}, {@code "0800"})
     * @param cavok {@code true} when the METAR explicitly reports {@code CAVOK}
     */
    public record Visibility(
            String value,
            boolean cavok
    ) {}

    /**
     * Air temperature and dew point.
     *
     * @param tempC     outside air temperature in degrees Celsius
     * @param dewpointC dew-point temperature in degrees Celsius
     */
    public record Temperature(
            Double tempC,
            Double dewpointC
    ) {}

    /**
     * Altimeter setting / QNH.
     *
     * @param qnhHpa QNH in hectopascal (hPa / mbar), converted from the NOAA altimeter value
     */
    public record Pressure(
            Double qnhHpa
    ) {}

    /**
     * A single cloud layer reported in the METAR.
     *
     * @param cover  sky coverage abbreviation: {@code FEW}, {@code SCT}, {@code BKN}, {@code OVC}
     * @param baseFt cloud base height in feet above aerodrome elevation
     */
    public record CloudLayer(
            String cover,
            Integer baseFt
    ) {}
}