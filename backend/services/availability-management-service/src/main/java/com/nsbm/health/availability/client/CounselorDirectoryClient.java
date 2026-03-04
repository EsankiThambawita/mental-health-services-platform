package com.nsbm.health.availability.client;

import com.nsbm.health.availability.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class CounselorDirectoryClient {

    private static final Logger log = LoggerFactory.getLogger(CounselorDirectoryClient.class);

    private final WebClient webClient;
    private final String baseUrl;

    public CounselorDirectoryClient(
            WebClient webClient,
            @Value("${services.counselor-directory.base-url}") String baseUrl
    ) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    /**
     * Calls Counselor Directory Service to check if counselor exists.
     * Assumes endpoint: GET {baseUrl}/api/v1/counselors/{id}
     */
    public void validateCounselorExists(String counselorId) {
        try {
            webClient.get()
                    .uri(baseUrl + "/api/v1/counselors/{id}", counselorId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BadRequestException("Invalid counselorId. Counselor not found: " + counselorId);
            }
            log.error("Counselor Directory call failed: status={}, body={}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new BadRequestException("Unable to validate counselor right now. Try again.");
        }
    }
}