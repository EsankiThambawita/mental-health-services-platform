package com.nsbm.health.availability.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "availability_slots")
@CompoundIndex(name = "idx_counselor_date", def = "{'counselorId': 1, 'date': 1}")
public class AvailabilitySlot {

    @Id
    private String availabilityId;

    private String counselorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AvailabilityStatus status;

    /** Required by Spring Data / MongoDB mapping */
    public AvailabilitySlot() {
    }

    /** Full constructor (useful for tests and explicit mapping) */
    public AvailabilitySlot(String availabilityId,
                            String counselorId,
                            LocalDate date,
                            LocalTime startTime,
                            LocalTime endTime,
                            AvailabilityStatus status) {
        this.availabilityId = availabilityId;
        this.counselorId = counselorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    /**
     * Convenience constructor for creating a new slot.
     * Defaults status to AVAILABLE.
     */
    public AvailabilitySlot(String counselorId,
                            LocalDate date,
                            LocalTime startTime,
                            LocalTime endTime) {
        this.counselorId = counselorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AvailabilityStatus.AVAILABLE;
    }

    public String getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(String availabilityId) {
        this.availabilityId = availabilityId;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }

    /* =========================
       BUSINESS HELPER METHODS
       ========================= */

    public boolean isBooked() {
        return this.status == AvailabilityStatus.BOOKED;
    }

    public void markBooked() {
        this.status = AvailabilityStatus.BOOKED;
    }

    public boolean isAvailable() {
        return this.status == AvailabilityStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "AvailabilitySlot{" +
                "availabilityId='" + availabilityId + '\'' +
                ", counselorId='" + counselorId + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
}