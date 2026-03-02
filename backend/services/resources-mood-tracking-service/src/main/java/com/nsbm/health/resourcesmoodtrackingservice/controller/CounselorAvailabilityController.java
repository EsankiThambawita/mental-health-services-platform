package com.nsbm.health.resourcesmoodtrackingservice.controller;

import com.nsbm.health.resourcesmoodtrackingservice.dto.CounselorAvailabilityResponse;
import com.nsbm.health.resourcesmoodtrackingservice.service.AvailabilityClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for fetching counselor availability from the Availability Management Service.
 * This demonstrates inter-service communication between microservices.
 */
@RestController
@RequestMapping("/v1/counselor-availability")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Counselor Availability", description = "APIs to fetch counselor availability from the Availability Management Service")
public class CounselorAvailabilityController {

    private final AvailabilityClientService availabilityClientService;

    /**
     * GET - Fetch availability slots for a specific counselor on a given date.
     *
     * Example: GET /api/v1/counselor-availability?counselorId=abc123&date=2026-03-10
     *
     * This calls the Availability Management Service internally.
     */
    @Operation(
            summary = "Get counselor availability by counselor ID and date",
            description = "Fetches availability slots from the Availability Management Service for a specific counselor on a given date"
    )
    @GetMapping
    public ResponseEntity<List<CounselorAvailabilityResponse>> getCounselorAvailability(
            @RequestParam String counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<CounselorAvailabilityResponse> slots = availabilityClientService.getCounselorAvailability(counselorId, date);

        if (slots.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(slots);
    }

    /**
     * GET - Fetch all available (not booked) slots on a given date across all counselors.
     *
     * Example: GET /api/v1/counselor-availability/available?date=2026-03-10
     *
     * This calls the Availability Management Service internally.
     */
    @Operation(
            summary = "Get all available slots by date",
            description = "Fetches all AVAILABLE (not booked) slots from the Availability Management Service for a given date"
    )
    @GetMapping("/available")
    public ResponseEntity<List<CounselorAvailabilityResponse>> getAvailableSlotsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<CounselorAvailabilityResponse> slots = availabilityClientService.getAvailableSlotsByDate(date);

        if (slots.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(slots);
    }
}
