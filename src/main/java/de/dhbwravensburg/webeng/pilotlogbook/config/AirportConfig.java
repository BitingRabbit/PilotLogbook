package de.dhbwravensburg.webeng.pilotlogbook.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AirportConfig {

    @Bean
    public RestClient ninjaAirportRestClient(
            @Value("${airport.api.base-url}") String baseUrl,
            @Value("${airport.api.timeout-ms}") int timeoutMS,
            @Value("${airport.api.key}") String apiKey) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMS);
        requestFactory.setReadTimeout(timeoutMS);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("X-API-Key", apiKey)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
