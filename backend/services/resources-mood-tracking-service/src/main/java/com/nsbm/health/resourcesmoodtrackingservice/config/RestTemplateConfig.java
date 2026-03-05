package com.nsbm.health.resourcesmoodtrackingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// REST template for inter-service calls
@Configuration
public class RestTemplateConfig {

    // Create REST template bean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
