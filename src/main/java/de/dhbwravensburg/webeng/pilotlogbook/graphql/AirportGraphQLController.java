package de.dhbwravensburg.webeng.pilotlogbook.graphql;

import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AirportResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AirportGraphQLController {

    private final AirportService airportService;

    @QueryMapping
    public AirportResponse airport(@Argument String icao) {
        return airportService.getByIcao(icao);
    }
}
