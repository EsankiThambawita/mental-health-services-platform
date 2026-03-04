package com.nsbm.health.appointment.dto;

import com.nsbm.health.appointment.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;

/** Request body for POST /api/v1/appointments */
public class BookAppointmentRequest {

    @NotBlank(message = "availabilityId is required")
    private String availabilityId;

    @NotBlank(message = "userName is required")
    private String userName;

    public BookAppointmentRequest() {
    }

    public BookAppointmentRequest(String availabilityId, String userName) {
        this.availabilityId = availabilityId;
        this.userName = userName;
    }

    public String getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(String availabilityId) { this.availabilityId = availabilityId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}