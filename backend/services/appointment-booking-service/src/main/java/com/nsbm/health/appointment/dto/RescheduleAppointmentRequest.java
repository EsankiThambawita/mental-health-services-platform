package com.nsbm.health.appointment.dto;

import com.nsbm.health.appointment.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;

/** Request body for PATCH /api/v1/appointments/{id}/reschedule */
public class RescheduleAppointmentRequest {

    @NotBlank(message = "newAvailabilityId is required")
    private String newAvailabilityId;

    public RescheduleAppointmentRequest() {
    }

    public RescheduleAppointmentRequest(String newAvailabilityId) {
        this.newAvailabilityId = newAvailabilityId;
    }

    public String getNewAvailabilityId() { return newAvailabilityId; }
    public void setNewAvailabilityId(String newAvailabilityId) { this.newAvailabilityId = newAvailabilityId; }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}