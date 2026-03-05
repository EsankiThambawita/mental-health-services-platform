package com.nsbm.health.resourcesmoodtrackingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

// Setup Swagger/OpenAPI documentation
@Configuration
public class OpenApiConfig {

    // Configure OpenAPI info
    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8081/api");
        server.setDescription("Local development server");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Resources & Mood Tracking Service API")
                        .version("1.0")
                        .description("API for managing mood entries, analytics, and mental health resources")
                        .contact(new Contact()
                                .name("Mental Health Platform Team")
                                .email("support@mentalhealthplatform.com")));
    }
}

