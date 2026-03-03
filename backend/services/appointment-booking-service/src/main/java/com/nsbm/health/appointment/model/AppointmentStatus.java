package com.nsbm.health.appointment.model;

/**
 * Lifecycle states of an Appointment.
 * CONFIRMED  - appointment successfully booked
 * CANCELLED  - appointment was cancelled
 * RESCHEDULED - appointment moved to a new slot
 */
public enum AppointmentStatus {
    CONFIRMED,
    CANCELLED,
    RESCHEDULED
}