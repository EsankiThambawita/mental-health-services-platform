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

/**
 * Service for communicating with the Availability Management Service.
 * Uses RestTemplate to make HTTP calls to fetch counselor availability slots.
 */
@Service
@Slf4j
public class AvailabilityClientService {

    private final RestTemplate restTemplate;

    // Base URL of the Availability Management Service (configured in application.yaml)
    @Value("${services.availability.base-url}")
    private String availabilityBaseUrl;

    public AvailabilityClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetch availability slots for a specific counselor on a given date.
     *
     * Calls: GET {availability-service}/api/v1/availability?counselorId={counselorId}&date={date}
     *
     * @param counselorId the counselor's ID
     * @param date        the date to check availability for
     * @return list of availability slots, or empty list if the service is unavailable
     */
    public List<CounselorAvailabilityResponse> getCounselorAvailability(String counselorId, LocalDate date) {
        String url = availabilityBaseUrl + "/api/v1/availability?counselorId=" + counselorId + "&date=" + date;

        log.info("Calling Availability Management Service: {}", url);

        try {
            // Make the GET request and map the JSON array to a list of CounselorAvailabilityResponse
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
            // If the availability service is down or unreachable, log the error and return empty
            log.error("Failed to fetch availability from Availability Management Service: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetch all available (not booked) slots on a given date across all counselors.
     *
     * Calls: GET {availability-service}/api/v1/availability/available?date={date}
     *
     * @param date the date to check available slots for
     * @return list of available slots, or empty list if the service is unavailable
     */
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

