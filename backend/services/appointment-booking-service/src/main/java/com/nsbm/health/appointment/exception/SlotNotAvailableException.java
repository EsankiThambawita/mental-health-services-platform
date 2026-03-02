package com.nsbm.health.appointment.exception;

/** Thrown when the Availability service returns 409 - slot is already BOOKED. Results in HTTP 409. */
public class SlotNotAvailableException extends RuntimeException {
    public SlotNotAvailableException(String message) {
        super(message);
    }
}