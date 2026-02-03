package com.nsbm.health.availability.controller;

import com.nsbm.health.availability.dto.AvailabilityResponse;
import com.nsbm.health.availability.dto.CreateAvailabilityRequest;
import com.nsbm.health.availability.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@Tag(name = "Availability", description = "Manage counselor availability slots")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @Operation(summary = "Create an availability slot (non-overlapping)")
    @PostMapping
    public ResponseEntity<AvailabilityResponse> create(@Valid @RequestBody CreateAvailabilityRequest request) {
        AvailabilityResponse created = availabilityService.createAvailability(request);
        return ResponseEntity
                .created(URI.create("/api/availability/" + created.getAvailabilityId()))
                .body(created);
    }

    @Operation(summary = "Query availability by counselor and date")
    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> getByCounselorAndDate(
            @RequestParam String counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(availabilityService.getAvailability(counselorId, date));
    }

    @Operation(summary = "Book an availability slot (sets status to BOOKED)")
    @PutMapping("/{id}/book")
    public ResponseEntity<AvailabilityResponse> book(@PathVariable("id") String id) {
        return ResponseEntity.ok(availabilityService.bookAvailability(id));
    }
}
