package com.nsbm.health.resourcesmoodtrackingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate used to call other microservices.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate bean for inter-service communication.
     * Used to call the Availability Management Service and other services.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
