package de.dhbwravensburg.webeng.pilotlogbook;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Pilot Logbook API",
                version = "0.1.0",
                description = "API for managing pilot logbooks, including flights, aircraft, and airports.",
                contact = @Contact(
                        name = "DHBW Ravensburg WebEng II Team",
                        email = "christoph.gahabka@web.de"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT obtained from POST /api/v1/auth/login or /register"
)
@SpringBootApplication
public class PilotLogbookApplication {

    public static void main(String[] args) {
        SpringApplication.run(PilotLogbookApplication.class, args);
    }

}
