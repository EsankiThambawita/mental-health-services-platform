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

// Fetches counselor availability from the Availability Management Service
@RestController
@RequestMapping("/v1/counselor-availability")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Counselor Availability", description = "APIs to fetch counselor availability from the Availability Management Service")
public class CounselorAvailabilityController {

    private final AvailabilityClientService availabilityClientService;

    // Get availability for a specific counselor on a date
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

    // Get all available (not booked) slots on a date
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
