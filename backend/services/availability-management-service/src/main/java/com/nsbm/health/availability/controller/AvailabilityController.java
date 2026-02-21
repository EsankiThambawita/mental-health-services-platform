package com.nsbm.health.availability.controller;

import com.nsbm.health.availability.dto.AvailabilityResponse;
import com.nsbm.health.availability.dto.CreateAvailabilityRequest;
import com.nsbm.health.availability.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing counselor availability slots.
 *
 * Base path: /api/v1/availability
 * Supports:
 * - Creating non-overlapping availability slots
 * - Querying availability for a counselor on a specific date
 * - Booking an availability slot
 */
@RestController
@RequestMapping(AvailabilityController.BASE_PATH)
@Tag(name = "Availability", description = "Manage counselor availability slots")
public class AvailabilityController {

    public static final String BASE_PATH = "/api/v1/availability";

    private static final Logger log = LoggerFactory.getLogger(AvailabilityController.class);

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @Operation(summary = "Create an availability slot (non-overlapping)")
    @PostMapping
    public ResponseEntity<AvailabilityResponse> create(@Valid @RequestBody CreateAvailabilityRequest request) {
        log.info("Create availability request received: {}", request);

        AvailabilityResponse created = availabilityService.createAvailability(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()          // /api/v1/availability
                .path("/{id}")                 // /{id}
                .buildAndExpand(created.getAvailabilityId())
                .toUri();

        log.info("Availability created: availabilityId={}, location={}", created.getAvailabilityId(), location);

        return ResponseEntity
                .created(location)
                .body(created);
    }

    @Operation(summary = "Query availability by counselor and date")
    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> getByCounselorAndDate(
            @RequestParam String counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("Get availability request: counselorId={}, date={}", counselorId, date);

        List<AvailabilityResponse> results = availabilityService.getAvailability(counselorId, date);

        log.info("Availability query result count: {}", results.size());

        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Book an availability slot (sets status to BOOKED)")
    @PutMapping("/{id}/book")
    public ResponseEntity<AvailabilityResponse> book(@PathVariable("id") String id) {
        log.info("Book availability request: availabilityId={}", id);

        AvailabilityResponse booked = availabilityService.bookAvailability(id);

        log.info("Availability booked: availabilityId={}, status={}", id, booked.getStatus());

        return ResponseEntity.ok(booked);
    }
}