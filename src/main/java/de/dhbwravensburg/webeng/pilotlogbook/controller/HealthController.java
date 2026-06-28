package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.model.HealthStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Health", description = "Liveness probe for monitoring and container orchestration")
public class HealthController {

    @Operation(
            summary = "Liveness probe",
            description = "Returns a constant payload indicating the service is up. Used by Docker/Compose healthchecks."
    )
    @ApiResponse(responseCode = "200", description = "Service is up")
    @GetMapping("/api/v1/health")
    public HealthStatus health() {
        return new HealthStatus(
                "UP",
                "Pilot Logbook",
                "1.0.0"
        );
    }
}