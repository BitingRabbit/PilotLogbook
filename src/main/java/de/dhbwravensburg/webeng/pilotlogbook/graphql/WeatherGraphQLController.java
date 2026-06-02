package de.dhbwravensburg.webeng.pilotlogbook.graphql;

import de.dhbwravensburg.webeng.pilotlogbook.dto.response.MetarResponse;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.WeatherSnapshotResponse;
import de.dhbwravensburg.webeng.pilotlogbook.service.WeatherService;
import de.dhbwravensburg.webeng.pilotlogbook.service.WeatherSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class WeatherGraphQLController {

    private final WeatherService weatherService;
    private final WeatherSnapshotService weatherSnapshotService;

    @QueryMapping
    public MetarResponse metar(@Argument String icao, @Argument LocalDateTime time) {
        return time == null
                ? weatherService.getLiveMetar(icao.toUpperCase())
                : weatherService.getHistoricalMetar(icao.toUpperCase(), time);
    }

    @MutationMapping
    public List<WeatherSnapshotResponse> refreshWeatherSnapshots(@Argument Long flightId) {
        return weatherSnapshotService.refreshSnapshots(flightId);
    }
}
