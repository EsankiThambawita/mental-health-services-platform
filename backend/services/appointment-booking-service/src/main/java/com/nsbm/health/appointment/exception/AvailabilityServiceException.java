package com.nsbm.health.appointment.exception;

/** Thrown when the Availability Management Service is unreachable or returns an error. Results in HTTP 503. */
public class AvailabilityServiceException extends RuntimeException {
    public AvailabilityServiceException(String message) {
        super(message);
    }
}