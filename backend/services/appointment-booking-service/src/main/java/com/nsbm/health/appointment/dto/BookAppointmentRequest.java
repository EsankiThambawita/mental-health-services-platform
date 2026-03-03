package com.nsbm.health.appointment.dto;

import com.nsbm.health.appointment.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;

/** Request body for POST /api/v1/appointments */
public class BookAppointmentRequest {

    @NotBlank(message = "availabilityId is required")
    private String availabilityId;

    @NotBlank(message = "userId is required")
    private String userId;

    public BookAppointmentRequest() {
    }

    public BookAppointmentRequest(String availabilityId, String userId) {
        this.availabilityId = availabilityId;
        this.userId = userId;
    }

    public String getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(String availabilityId) { this.availabilityId = availabilityId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}