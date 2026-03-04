package com.nsbm.health.appointment.client;

import com.nsbm.health.appointment.client.dto.AvailabilityResponse;
import com.nsbm.health.appointment.exception.AvailabilityServiceException;
import com.nsbm.health.appointment.exception.SlotNotAvailableException;
import com.nsbm.health.appointment.exception.SlotNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

/**
 * HTTP client that communicates with the Availability Management Service.
 *
 * This is the ONLY place in this service that contacts the Availability service.
 * It calls PUT /api/v1/availability/{id}/book to atomically lock a slot.
 *
 * Base URL is configured via availability.base-url in application.yml.
 * Currently points to http://localhost:8082 (availability service port).
 */
@Component
public class AvailabilityClient {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityClient.class);

    private final WebClient webClient;

    public AvailabilityClient(@Value("${availability.base-url}") String baseUrl,
                              WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Calls PUT /api/v1/availability/{id}/book on the Availability Management Service.
     * This atomically marks the slot as BOOKED and returns the updated slot details.
     *
     * @param availabilityId the ID of the slot to book
     * @return AvailabilityResponse with the booked slot details (counselorId, date, times)
     * @throws SlotNotFoundException       if availability service returns 404
     * @throws SlotNotAvailableException   if availability service returns 409 (already booked)
     * @throws AvailabilityServiceException if service is unreachable or returns unexpected error
     */

    // =========================
    // BOOK SLOT
    // =========================
    public AvailabilityResponse bookSlot(String availabilityId) {
        log.info("Calling Availability Service - PUT /api/v1/availability/{}/book", availabilityId);

        try {
            AvailabilityResponse response = webClient.put()
                    .uri("/api/v1/availability/{id}/book", availabilityId)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(
                                            new SlotNotFoundException("Availability slot not found: " + availabilityId))))
                    .onStatus(
                            status -> status.value() == 409,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(
                                            new SlotNotAvailableException("Slot is already booked: " + availabilityId))))
                    .onStatus(
                            HttpStatusCode::isError,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(
                                            new AvailabilityServiceException("Availability service error: " + body))))
                    .bodyToMono(AvailabilityResponse.class)
                    .block();

            log.info("Availability Service responded - slot booked: availabilityId={}", availabilityId);
            return response;

        } catch (WebClientRequestException e) {
            log.error("Availability Service unreachable: {}", e.getMessage());
            throw new AvailabilityServiceException("Availability Management Service is currently unreachable.");
        }
    }

    // =========================
    // GET AVAILABLE SLOTS BY DATE
    // =========================
    public List<AvailabilityResponse> getAvailableSlotsByDate(LocalDate date) {

        log.info("Calling Availability Service - GET available slots for date={}", date);

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/availability/available")
                            .queryParam("date", date)
                            .build())
                    .retrieve()
                    .bodyToFlux(AvailabilityResponse.class)
                    .collectList()
                    .block();

        } catch (WebClientRequestException e) {
            throw new AvailabilityServiceException("Availability Management Service is currently unreachable.");
        }
    }
}