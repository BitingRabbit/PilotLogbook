package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.dto.external.NoaaMetarDto;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.MetarDto;
import de.dhbwravensburg.webeng.pilotlogbook.exception.WeatherUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestClient noaaWeatherRestClient;
    private final AirportService airportService;


    /**
     * Fetches the most recent METAR for the given airport. Used by the dashboard widget.
     *
     * @param icao 4-letter ICAO airport code (e.g. {@code EDDS})
     * @return parsed METAR with raw string and decoded fields
     * @throws WeatherUnavailableException if NOAA returns an error or no data
     */
    public MetarDto getLiveMetar(String icao) {
        return callNoaa(icao, null);
    }

    /**
     * Fetches the METAR closest to the given point in time. Used when capturing
     * weather snapshots for a flight's departure or arrival.
     *
     * @param icao 4-letter ICAO airport code (e.g. {@code EDDS})
     * @param time the requested observation time (interpreted as UTC)
     * @return parsed METAR with raw string and decoded fields
     * @throws WeatherUnavailableException if NOAA returns an error or no data
     */
    public MetarDto getHistoricalMetar(String icao, LocalDateTime time) {
        return callNoaa(icao, time);
    }

    /**
     * Calls the NOAA aviationweather.gov API and returns the first result.
     * Passes {@code time = null} for a live request, or a specific UTC time for historical data.
     *
     * @param icao 4-letter ICAO airport code
     * @param time UTC time for historical lookup, or {@code null} for the latest METAR
     * @throws WeatherUnavailableException if the API returns an error or an empty result
     */
    private MetarDto callNoaa(String icao, LocalDateTime time) {
        /* Validate wether ICAO codes are correct (Pattern) and existing */
        airportService.validateIcaoAndgetOrFetch(icao);

        NoaaMetarDto[] response;

        if (time == null) {
            response = noaaWeatherRestClient.get()
                    .uri("/metar?ids={icao}&format=json&hours=1", icao)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new WeatherUnavailableException("NOAA API error: " + res.getStatusCode());
                    })
                    .body(NoaaMetarDto[].class);
        } else {
            // NOAA expects UTC — aviation times are always UTC by convention
            String dateParam = time.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
            response = noaaWeatherRestClient.get()
                    .uri("/metar?ids={icao}&format=json&date={date}", icao, dateParam)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new WeatherUnavailableException("NOAA API error: " + res.getStatusCode());
                    })
                    .body(NoaaMetarDto[].class);
        }

        if (response == null || response.length == 0) {
            throw new WeatherUnavailableException("No METAR available for ICAO: " + icao);
        }

        return mapToMetarDto(response[0]);
    }

    /**
     * Maps a raw NOAA response object to the internal {@link MetarDto} representation.
     *
     * @param noaa the raw NOAA response
     * @return structured MetarDto with raw string and decoded fields
     */
    private MetarDto mapToMetarDto(NoaaMetarDto noaa) {
        // CAVOK (Clouds and Visibility OK) means no significant weather — skip cloud/visibility details
        boolean cavok = noaa.getRawOb() != null && noaa.getRawOb().contains("CAVOK");
        // wdir is null when wind is variable (VRB), also check raw string as fallback
        boolean variable = noaa.getWdir() == null
                || (noaa.getRawOb() != null && noaa.getRawOb().contains("VRB"));

        // NOAA returns obsTime as Unix epoch seconds — convert to UTC LocalDateTime
        LocalDateTime observationTime = noaa.getObsTime() != null
                ? Instant.ofEpochSecond(noaa.getObsTime()).atZone(ZoneOffset.UTC).toLocalDateTime()
                : null;

        List<MetarDto.CloudLayer> clouds = noaa.getClouds() == null ? List.of() :
                noaa.getClouds().stream()
                        .map(c -> new MetarDto.CloudLayer(c.getCover(), c.getBase()))
                        .toList();

        List<String> phenomena = (noaa.getWxString() == null || noaa.getWxString().isBlank())
                ? List.of()
                // wxString contains space-separated codes, e.g. "-RA BR" → ["-RA", "BR"]
                : Arrays.asList(noaa.getWxString().split("\\s+"));

        MetarDto.DecodedMetar decoded = new MetarDto.DecodedMetar(
                new MetarDto.Wind(variable ? null : noaa.getWdir(), noaa.getWspd(), noaa.getWgst(), variable),
                new MetarDto.Visibility(noaa.getVisib(), cavok),
                new MetarDto.Temperature(noaa.getTemp(), noaa.getDewp()),
                new MetarDto.Pressure(noaa.getAltim()),
                clouds,
                phenomena,
                noaa.getFltCat()
        );

        return new MetarDto(noaa.getIcao(), observationTime, noaa.getRawOb(), decoded);
    }
}