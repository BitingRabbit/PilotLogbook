package de.dhbwravensburg.webeng.pilotlogbook.graphql;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AircraftResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.AircraftService;
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
public class AircraftGraphQLController {

    private final AircraftService aircraftService;

    @QueryMapping("aircraft")
    public List<AircraftResponse> aircraft() {
        return aircraftService.getAllAircraft();
    }

    @QueryMapping
    public AircraftResponse aircraftById(@Argument Long id) {
        return aircraftService.getAircraftById(id);
    }

    @MutationMapping
    public AircraftResponse createAircraft(@Argument CreateAircraftRequest input) {
        return aircraftService.createAircraft(input);
    }

    @MutationMapping
    public AircraftResponse updateAircraft(@Argument Long id, @Argument UpdateAircraftRequest input) {
        return aircraftService.updateAircraft(id, input);
    }

    @MutationMapping
    public boolean deleteAircraft(@Argument Long id) {
        aircraftService.deleteAircraft(id);
        return true;
    }
}
