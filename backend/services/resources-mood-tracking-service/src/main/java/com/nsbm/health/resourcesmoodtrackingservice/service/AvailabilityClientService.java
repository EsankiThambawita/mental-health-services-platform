package com.nsbm.health.resourcesmoodtrackingservice.service;

import com.nsbm.health.resourcesmoodtrackingservice.dto.CounselorAvailabilityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

// Client for calling Availability Management Service
@Service
@Slf4j
public class AvailabilityClientService {

    private final RestTemplate restTemplate;

    // Base URL of Availability service (from config)
    @Value("${services.availability.base-url}")
    private String availabilityBaseUrl;

    public AvailabilityClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Fetch availability slots for a counselor on a date
    public List<CounselorAvailabilityResponse> getCounselorAvailability(String counselorId, LocalDate date) {
        String url = availabilityBaseUrl + "/api/v1/availability?counselorId=" + counselorId + "&date=" + date;

        log.info("Calling Availability Management Service: {}", url);

        try {
            ResponseEntity<List<CounselorAvailabilityResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CounselorAvailabilityResponse>>() {}
            );

            List<CounselorAvailabilityResponse> slots = response.getBody();
            log.info("Received {} availability slots from Availability Service", slots != null ? slots.size() : 0);

            return slots != null ? slots : Collections.emptyList();

        } catch (RestClientException ex) {
            log.error("Failed to fetch availability from Availability Management Service: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    // Fetch all available slots on a date (across all counselors)
    public List<CounselorAvailabilityResponse> getAvailableSlotsByDate(LocalDate date) {
        String url = availabilityBaseUrl + "/api/v1/availability/available?date=" + date;

        log.info("Calling Availability Management Service for available slots: {}", url);

        try {
            ResponseEntity<List<CounselorAvailabilityResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CounselorAvailabilityResponse>>() {}
            );

            List<CounselorAvailabilityResponse> slots = response.getBody();
            log.info("Received {} available slots from Availability Service", slots != null ? slots.size() : 0);

            return slots != null ? slots : Collections.emptyList();

        } catch (RestClientException ex) {
            log.error("Failed to fetch available slots from Availability Management Service: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}

