package com.nsbm.health.appointment.client.dto;

/**
 * Mirrors the AvailabilityStatus enum from the Availability Management Service.
 * Used to deserialize the status field in their API response.
 */
public enum AvailabilityStatus {
    AVAILABLE,
    BOOKED
}