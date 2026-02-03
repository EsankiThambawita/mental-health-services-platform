package com.nsbm.health.availability.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI availabilityOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Availability Management Service API")
                        .version("v1")
                        .description("Manages counselor availability slots for the appointment booking platform."));
    }
}
