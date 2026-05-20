package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.dto.external.NinjaAirportDto;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AirportResponse;
import de.dhbwravensburg.webeng.pilotlogbook.exception.AirportUnavailableException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Airport;
import de.dhbwravensburg.webeng.pilotlogbook.model.Runway;
import de.dhbwravensburg.webeng.pilotlogbook.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;
    private final RestClient ninjaAirportRestClient;
    private static final java.util.regex.Pattern ICAO_PATTERN = java.util.regex.Pattern.compile("^[A-Z]{4}$");

    /**
     * Returns airport data for the given ICAO code.
     * First checks the local DB cache; if not found, fetches from api-ninjas,
     * persists the result, and returns it. Idempotent
     *
     * @param icao 4-letter uppercase ICAO code
     * @return cached or freshly fetched airport entity
     * @throws IllegalArgumentException    if the ICAO format is invalid or unknown
     * @throws AirportUnavailableException if the external API is unreachable
     */
    public Airport validateIcaoAndGetOrFetch(String icao) {
        String icaoUpperCase = icao.toUpperCase();

        if (!ICAO_PATTERN.matcher(icaoUpperCase).matches()) {
            throw new IllegalArgumentException("Invalid ICAO format: " + icaoUpperCase);
        }
        return airportRepository.findByIcao(icaoUpperCase)
                .orElseGet(() -> {
                    Airport fetched = fetchFromNinja(icaoUpperCase);
                    return airportRepository.save(fetched);
                });
    }

    /**
     * Returns the {@link AirportResponse} DTO for the given ICAO code.
     * Delegates to {@link #validateIcaoAndGetOrFetch(String)} — use this in the controller.
     *
     * @param icao 4-letter uppercase ICAO code
     * @return airport response DTO
     */
    public AirportResponse getByIcao(String icao) {
        return AirportResponse.from(validateIcaoAndGetOrFetch(icao));
    }

    // ------------------------ HELPER ------------------------

    /**
     * Calls api-ninjas to retrieve airport data.
     *
     * @param icao validated, uppercase ICAO code
     * @return unmapped entity ready for persistence
     * @throws IllegalArgumentException    if the API returns no result for this ICAO
     * @throws AirportUnavailableException if the API returns an HTTP error or is unreachable
     */
    private Airport fetchFromNinja(String icao) {
        NinjaAirportDto[] result;
        try {
            result = ninjaAirportRestClient.get()
                    .uri(uri -> uri.queryParam("icao", icao).build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        throw new AirportUnavailableException(
                                "Airport API error for " + icao + ": " + resp.getStatusCode());
                    })
                    .body(NinjaAirportDto[].class);
        } catch (RestClientException ex) {
            throw new AirportUnavailableException("Airport API unreachable for " + icao);
        }

        if (result == null || result.length == 0) {
            throw new IllegalArgumentException("Unknown ICAO: " + icao);
        }

        return mapToEntity(result[0]);
    }

    /**
     * Maps a raw api-ninjas DTO to the {@link Airport} entity.
     * Ninja delivers latitude/longitude as {@code Double}, size as a lowercase string.
     */
    private Airport mapToEntity(NinjaAirportDto dto) {
        Set<Runway> runways = dto.getRunways() == null ? Set.of() :
                dto.getRunways().stream()
                        .map(r -> new Runway(r.getLength(), r.getWidth(), r.getHasLights()))
                        .collect(Collectors.toSet());

        return Airport.builder()
                .icao(dto.getIcao())
                .iata(dto.getIata())
                .name(dto.getName())
                .city(dto.getCity())
                .country(dto.getCountry())
                .elevationInFt(dto.getElevationFt())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .timezone(dto.getTimezone())
                .size(parseSize(dto.getSize()))
                .runways(runways)
                .build();
    }

    /**
     * Converts the Ninja size string ({@code "small"}, {@code "medium"}, {@code "large"})
     * to the {@link Airport.Size} enum. Returns {@code null} for unknown/null values.
     */
    private Airport.Size parseSize(String size) {
        if (size == null) return null;
        return switch (size.toLowerCase()) {
            case "small"  -> Airport.Size.SMALL;
            case "medium" -> Airport.Size.MEDIUM;
            case "large"  -> Airport.Size.LARGE;
            default       -> null;
        };
    }
}
