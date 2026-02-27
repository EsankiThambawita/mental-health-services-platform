package com.nsbm.health.counselor.model;

/**
 * Embedded document for availability
 */
public class Availability {

    private String day;
    private String timeSlot;

    // Getter for day
    public String getDay() {
        return day;
    }

    // Setter for day
    public void setDay(String day) {
        this.day = day;
    }

    // Getter for timeSlot
    public String getTimeSlot() {
        return timeSlot;
    }

    // Setter for timeSlot
    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}

