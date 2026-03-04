package com.nsbm.health.resourcesmoodtrackingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO to map the response from the Availability Management Service.
 * Represents a counselor's availability slot.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Counselor availability slot from the Availability Management Service")
public class CounselorAvailabilityResponse {

    @Schema(description = "Unique identifier of the availability slot")
    private String availabilityId;

    @Schema(description = "Counselor ID who owns this slot")
    private String counselorId;

    @Schema(description = "Date of the availability slot")
    private LocalDate date;

    @Schema(description = "Start time of the slot")
    private LocalTime startTime;

    @Schema(description = "End time of the slot")
    private LocalTime endTime;

    @Schema(description = "Slot status: AVAILABLE or BOOKED")
    private String status;
}

