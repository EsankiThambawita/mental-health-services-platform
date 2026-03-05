package com.nsbm.health.appointment.dto;

import com.nsbm.health.appointment.model.AppointmentStatus;
import com.nsbm.health.appointment.util.ToStringUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/** Response DTO returned to API consumers for all appointment endpoints. */
public class AppointmentResponse {

    private String id;
    private String availabilityId;
    private String userName;
    private String counselorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String cancelReason;
    private Instant createdAt;
    private Instant updatedAt;

    public AppointmentResponse() {
    }

    public AppointmentResponse(String id, String availabilityId, String userName,
                               String counselorId, LocalDate date, LocalTime startTime,
                               LocalTime endTime, AppointmentStatus status,
                               String cancelReason, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.availabilityId = availabilityId;
        this.userName = userName;
        this.counselorId = counselorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.cancelReason = cancelReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(String availabilityId) { this.availabilityId = availabilityId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCounselorId() { return counselorId; }
    public void setCounselorId(String counselorId) { this.counselorId = counselorId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}