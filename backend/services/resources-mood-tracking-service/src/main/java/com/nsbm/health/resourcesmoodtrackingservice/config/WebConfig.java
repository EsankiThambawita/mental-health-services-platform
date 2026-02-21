package com.nsbm.health.resourcesmoodtrackingservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration for CORS and other settings
 * This allows all services to communicate with each other
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow API endpoints - all the main routes
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);

        // Allow Swagger/OpenAPI documentation endpoints
        registry.addMapping("/v3/api-docs")
                .allowedOrigins("*")
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);

        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("*")
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);

        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("*")
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);

        registry.addMapping("/swagger-ui.html")
                .allowedOrigins("*")
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}

