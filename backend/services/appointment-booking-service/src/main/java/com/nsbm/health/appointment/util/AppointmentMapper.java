package com.nsbm.health.appointment.util;

import com.nsbm.health.appointment.dto.AppointmentResponse;
import com.nsbm.health.appointment.model.Appointment;

/** Maps Appointment entity to AppointmentResponse DTO. */
public final class AppointmentMapper {

    private AppointmentMapper() {
    }

    public static AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse r = new AppointmentResponse();
        r.setId(a.getId());
        r.setAvailabilityId(a.getAvailabilityId());
        r.setUserId(a.getUserId());
        r.setCounselorId(a.getCounselorId());
        r.setDate(a.getDate());
        r.setStartTime(a.getStartTime());
        r.setEndTime(a.getEndTime());
        r.setStatus(a.getStatus());
        r.setCancelReason(a.getCancelReason());
        r.setCreatedAt(a.getCreatedAt());
        r.setUpdatedAt(a.getUpdatedAt());
        return r;
    }
}