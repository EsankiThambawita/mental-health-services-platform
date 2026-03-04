package com.nsbm.health.appointment.model;

import com.nsbm.health.appointment.util.ToStringUtil;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * MongoDB document representing a booked appointment.
 * availabilityId carries a unique index to prevent double-booking at DB level.
 */
@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    @Indexed(unique = true)
    private String availabilityId;

    private String userId;
    private String counselorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String cancelReason;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /** Required by Spring Data MongoDB for deserialization. */
    public Appointment() {
    }

    /** Full constructor for creating a new appointment. */
    public Appointment(String availabilityId, String userId, String counselorId,
                       LocalDate date, LocalTime startTime, LocalTime endTime,
                       AppointmentStatus status) {
        this.availabilityId = availabilityId;
        this.userId = userId;
        this.counselorId = counselorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(String availabilityId) { this.availabilityId = availabilityId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

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