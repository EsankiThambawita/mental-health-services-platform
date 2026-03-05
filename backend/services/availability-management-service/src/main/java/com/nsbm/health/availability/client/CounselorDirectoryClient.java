
package com.nsbm.health.availability.client;

import com.nsbm.health.availability.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class CounselorDirectoryClient {

    private static final Logger log = LoggerFactory.getLogger(CounselorDirectoryClient.class);

    private final RestTemplate restTemplate;

    @Value("${services.counselor-directory.base-url}")
    private String baseUrl;

    public CounselorDirectoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validateCounselorExists(String counselorId) {
        try {
            String url = baseUrl + "/api/counselors/" + counselorId;
            log.info("Calling counselor-directory-service: {}", url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BadRequestException("Invalid counselorId (counselor not found)");
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BadRequestException("Invalid counselorId (counselor not found)");
            }
            throw new BadRequestException("Counselor directory error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            log.warn("Counselor service unreachable, skipping validation for now: {}", e.getMessage());
        }
    }
}