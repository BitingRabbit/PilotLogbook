package de.dhbwravensburg.webeng.pilotlogbook.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Spring configuration providing a {@link RestClient} and {@link ObjectMapper} for the NOAA weather API.
 *
 * <p> Config properties:
 * <ul>
 *   <li>{@code weather.api.base-url} base URL of the NOAA API (e.g. {@code https://aviationweather.gov/api/data})</li>
 *   <li>{@code weather.api.timeout-ms}  connect and read timeout in milliseconds</li>
 * </ul>
 */
@Configuration
public class WeatherConfig {

    /**
     * RestClient pre-configured for the NOAA Aviation Weather Center API.
     * Sets connect and read timeouts from {@code weather.api.timeout-ms} and
     * defaults the {@code Accept} header to {@code application/json}.
     *
     * @param baseUrl   NOAA API base URL from {@code weather.api.base-url}
     * @param timeoutMS connect and read timeout in milliseconds from {@code weather.api.timeout-ms}
     * @return configured RestClient bean
     */
    @Bean
    public RestClient noaaWeatherRestClient(
            @Value("${weather.api.base-url}") String baseUrl,
            @Value("${weather.api.timeout-ms}") int timeoutMS) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMS);
        requestFactory.setReadTimeout(timeoutMS);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * ObjectMapper bean for JSON serialization/deserialization of decoded METAR data.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
