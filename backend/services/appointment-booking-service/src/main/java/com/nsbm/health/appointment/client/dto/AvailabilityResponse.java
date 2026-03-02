package com.nsbm.health.appointment.client.dto;

import com.nsbm.health.appointment.util.ToStringUtil;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Local DTO that matches the response structure returned by the
 * Availability Management Service (PUT /api/v1/availability/{id}/book).
 *
 * Fields match exactly what their AvailabilityResponse returns:
 * availabilityId, counselorId, date, startTime, endTime, status
 */
public class AvailabilityResponse {

    private String availabilityId;
    private String counselorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AvailabilityStatus status;

    /** Required for JSON deserialization by WebClient/Jackson. */
    public AvailabilityResponse() {
    }

    public AvailabilityResponse(String availabilityId, String counselorId, LocalDate date,
                                LocalTime startTime, LocalTime endTime, AvailabilityStatus status) {
        this.availabilityId = availabilityId;
        this.counselorId = counselorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(String availabilityId) { this.availabilityId = availabilityId; }

    public String getCounselorId() { return counselorId; }
    public void setCounselorId(String counselorId) { this.counselorId = counselorId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public AvailabilityStatus getStatus() { return status; }
    public void setStatus(AvailabilityStatus status) { this.status = status; }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}