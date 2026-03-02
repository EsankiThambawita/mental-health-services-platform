package com.nsbm.health.appointment.dto;

import com.nsbm.health.appointment.util.ToStringUtil;

/** Request body for PATCH /api/v1/appointments/{id}/cancel. Cancel reason is optional. */
public class CancelAppointmentRequest {

    private String cancelReason;

    public CancelAppointmentRequest() {
    }

    public CancelAppointmentRequest(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}