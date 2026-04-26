package de.dhbwravensburg.webeng.pilotlogbook.model;

public record HealthStatus(
        String status,
        String application,
        String version
) {
}
