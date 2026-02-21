package com.nsbm.health.appointment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    private String userId;
    private String counselorId;
    private String slotId;

    private AppointmentStatus status;

    private String notes;

    private Instant createdAt;
    private Instant updatedAt;

    public Appointment() {
    }

    public Appointment(String id, String userId, String counselorId, String slotId,
                       AppointmentStatus status, String notes, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.counselorId = counselorId;
        this.slotId = slotId;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCounselorId() { return counselorId; }
    public void setCounselorId(String counselorId) { this.counselorId = counselorId; }

    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
