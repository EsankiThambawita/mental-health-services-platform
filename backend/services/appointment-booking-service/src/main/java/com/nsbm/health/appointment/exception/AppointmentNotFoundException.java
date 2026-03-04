package com.nsbm.health.appointment.exception;

/** Thrown when an appointment ID does not exist in MongoDB. Results in HTTP 404. */
public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(String message) {
        super(message);
    }
}