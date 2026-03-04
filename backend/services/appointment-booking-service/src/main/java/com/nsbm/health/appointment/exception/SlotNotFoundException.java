package com.nsbm.health.appointment.exception;

/** Thrown when the Availability service returns 404 for a given slot ID. Results in HTTP 404. */
public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(String message) {
        super(message);
    }
}