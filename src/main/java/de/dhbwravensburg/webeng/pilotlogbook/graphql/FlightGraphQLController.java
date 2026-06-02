package de.dhbwravensburg.webeng.pilotlogbook.graphql;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateFlightRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.FlightResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FlightGraphQLController {

    private final FlightService flightService;

    @QueryMapping
    public List<FlightResponse> flights() {
        return flightService.getAllFlights();
    }

    @QueryMapping
    public FlightResponse flight(@Argument Long id) {
        return flightService.getFlightById(id);
    }

    @QueryMapping
    public List<FlightResponse> flightsDashboard(
            @Argument String dep,
            @Argument String dest,
            @Argument Integer duration,
            @Argument Integer month) {
        Long durationLong = duration == null ? null : duration.longValue();
        return flightService.getFlightsForDashboard(dep, dest, durationLong, month);
    }

    @MutationMapping
    public FlightResponse createFlight(@Argument CreateFlightRequest input) {
        return flightService.createFlight(input);
    }

    @MutationMapping
    public FlightResponse updateFlight(@Argument Long id, @Argument UpdateFlightRequest input) {
        return flightService.updateFlight(id, input);
    }

    @MutationMapping
    public boolean deleteFlight(@Argument Long id) {
        flightService.deleteFlight(id);
        return true;
    }
}
