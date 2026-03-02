package com.nsbm.health.appointment.util;

/** Application-wide string constants to avoid hard-coded literals in business logic. */
public final class AppConstants {

    private AppConstants() {
    }

    public static final String SLOT_ALREADY_BOOKED = "Availability slot is already booked";
    public static final String SLOT_NOT_FOUND = "Availability slot not found";
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found";
    public static final String AVAILABILITY_SERVICE_DOWN = "Availability Management Service is currently unreachable";
}