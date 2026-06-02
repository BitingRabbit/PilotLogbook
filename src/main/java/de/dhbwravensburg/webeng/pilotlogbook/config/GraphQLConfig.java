package de.dhbwravensburg.webeng.pilotlogbook.config;

import graphql.GraphQLContext;
import graphql.scalars.ExtendedScalars;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class GraphQLConfig {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final GraphQLScalarType DATE_TIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("ISO-8601 local date-time, e.g. 2026-05-25T14:30:00")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object v, GraphQLContext c, Locale l) {
                    return ((LocalDateTime) v).format(FMT);
                }
                @Override
                public LocalDateTime parseValue(Object input, GraphQLContext c, Locale l) {
                    return LocalDateTime.parse((String) input, FMT);
                }
            })
            .build();

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiring -> wiring
                .scalar(DATE_TIME)
                .scalar(ExtendedScalars.GraphQLBigDecimal);
    }
}