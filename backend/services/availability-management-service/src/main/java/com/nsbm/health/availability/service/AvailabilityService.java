package com.nsbm.health.availability.service;

import com.nsbm.health.availability.dto.AvailabilityResponse;
import com.nsbm.health.availability.dto.CreateAvailabilityRequest;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    AvailabilityResponse createAvailability(CreateAvailabilityRequest request);

    List<AvailabilityResponse> getAvailability(String counselorId, LocalDate date);

    AvailabilityResponse bookAvailability(String availabilityId);
}