package com.nsbm.health.appointment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger/OpenAPI documentation configuration. Accessible at /swagger-ui when running. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appointmentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Appointment Booking Service API")
                        .version("v1")
                        .description("Manages appointment booking for the Mental Health Support Platform."));
    }
}