/* =========================================================
   AvailabilityClient.java
   - Client for Availability Management Service
   - Supports:
     - bookSlot(id):   PUT /api/v1/availability/{id}/book
     - releaseSlot(id):PUT /api/v1/availability/{id}/release  (NEW)
     - getAvailableSlotsByDate(date): GET /api/v1/availability/available?date=...
   ========================================================= */
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

@Component
public class AvailabilityClient {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityClient.class);
    private final WebClient webClient;

    public AvailabilityClient(@Value("${availability.base-url}") String baseUrl,
                              WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /* ---------------------------------------------------------
       BOOK SLOT
       - PUT /api/v1/availability/{id}/book
       - 404 -> SlotNotFoundException
       - 409 -> SlotNotAvailableException (already booked)
       - Other 5xx/4xx -> AvailabilityServiceException
    ---------------------------------------------------------- */
    public AvailabilityResponse bookSlot(String availabilityId) {
        log.info("Calling Availability Service - PUT /api/v1/availability/{}/book", availabilityId);

        try {
            return webClient.put()
                    .uri("/api/v1/availability/{id}/book", availabilityId)
                    .retrieve()
                    .onStatus(s -> s.value() == 404,
                            r -> r.bodyToMono(String.class).flatMap(b ->
                                    Mono.error(new SlotNotFoundException("Availability slot not found: " + availabilityId))))
                    .onStatus(s -> s.value() == 409,
                            r -> r.bodyToMono(String.class).flatMap(b ->
                                    Mono.error(new SlotNotAvailableException("Slot is already booked: " + availabilityId))))
                    .onStatus(HttpStatusCode::isError,
                            r -> r.bodyToMono(String.class).flatMap(b ->
                                    Mono.error(new AvailabilityServiceException("Availability service error: " + b))))
                    .bodyToMono(AvailabilityResponse.class)
                    .block();
        } catch (WebClientRequestException e) {
            log.error("Availability Service unreachable: {}", e.getMessage());
            throw new AvailabilityServiceException("Availability Management Service is currently unreachable.");
        }
    }

    /* ---------------------------------------------------------
       RELEASE SLOT (NEW)
       - PUT /api/v1/availability/{id}/release
       - Used when appointment is cancelled/rescheduled
       - 404 -> SlotNotFoundException
       - Other 5xx/4xx -> AvailabilityServiceException
    ---------------------------------------------------------- */
    public AvailabilityResponse releaseSlot(String availabilityId) {
        log.info("Calling Availability Service - PUT /api/v1/availability/{}/release", availabilityId);

        try {
            return webClient.put()
                    .uri("/api/v1/availability/{id}/release", availabilityId)
                    .retrieve()
                    .onStatus(s -> s.value() == 404,
                            r -> r.bodyToMono(String.class).flatMap(b ->
                                    Mono.error(new SlotNotFoundException("Availability slot not found: " + availabilityId))))
                    .onStatus(HttpStatusCode::isError,
                            r -> r.bodyToMono(String.class).flatMap(b ->
                                    Mono.error(new AvailabilityServiceException("Availability service error: " + b))))
                    .bodyToMono(AvailabilityResponse.class)
                    .block();
        } catch (WebClientRequestException e) {
            log.error("Availability Service unreachable: {}", e.getMessage());
            throw new AvailabilityServiceException("Availability Management Service is currently unreachable.");
        }
    }

    /* ---------------------------------------------------------
       GET AVAILABLE SLOTS BY DATE
       - GET /api/v1/availability/available?date=YYYY-MM-DD
    ---------------------------------------------------------- */
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