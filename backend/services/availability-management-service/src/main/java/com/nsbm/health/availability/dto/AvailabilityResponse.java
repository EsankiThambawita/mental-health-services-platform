package com.nsbm.health.availability.dto;

import com.nsbm.health.availability.model.AvailabilityStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilityResponse {

    private String availabilityId;
    private String counselorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AvailabilityStatus status;

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

    @Override
    public String toString() {
        return "AvailabilityResponse{" +
                "availabilityId='" + availabilityId + '\'' +
                ", counselorId='" + counselorId + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
}
