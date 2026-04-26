package de.dhbwravensburg.webeng.pilotlogbook.controller;

import de.dhbwravensburg.webeng.pilotlogbook.model.HealthStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public HealthStatus health() {
        return new HealthStatus(
                "UP",
                "Pilot Logbook",
                "0.1.0"
        );
    }
}
