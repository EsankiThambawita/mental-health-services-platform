package com.nsbm.health.availability.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilitySlotDTO {

    private String id;
    private String counselorId;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isBooked;

    public AvailabilitySlotDTO() {}

    public AvailabilitySlotDTO(String id, String counselorId, String title,
                               LocalDate date, LocalTime startTime, LocalTime endTime,
                               Boolean isBooked) {
        this.id = id;
        this.counselorId = counselorId;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = isBooked;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCounselorId() { return counselorId; }
    public void setCounselorId(String counselorId) { this.counselorId = counselorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Boolean getIsBooked() { return isBooked; }
    public void setIsBooked(Boolean booked) { this.isBooked = booked; }

    // toString
    @Override
    public String toString() {
        return "AvailabilitySlotDTO{" +
                "id='" + id + '\'' +
                ", counselorId='" + counselorId + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isBooked=" + isBooked +
                '}';
    }
}
