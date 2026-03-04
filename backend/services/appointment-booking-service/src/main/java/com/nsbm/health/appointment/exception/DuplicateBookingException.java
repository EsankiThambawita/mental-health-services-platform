package com.nsbm.health.appointment.exception;

/** Thrown when a slot is already booked in our own DB (service-level or unique index violation). Results in HTTP 409. */
public class DuplicateBookingException extends RuntimeException {
    public DuplicateBookingException(String message) {
        super(message);
    }
}