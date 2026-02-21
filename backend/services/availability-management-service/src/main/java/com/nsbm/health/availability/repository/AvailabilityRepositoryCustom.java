package com.nsbm.health.availability.repository;

import com.nsbm.health.availability.model.AvailabilitySlot;

import java.util.Optional;

public interface AvailabilityRepositoryCustom {

    /**
     * Atomically marks the slot as BOOKED only if it's currently AVAILABLE.
     * This prevents double-booking under concurrent requests.
     */
    Optional<AvailabilitySlot> bookIfAvailable(String availabilityId);
}
