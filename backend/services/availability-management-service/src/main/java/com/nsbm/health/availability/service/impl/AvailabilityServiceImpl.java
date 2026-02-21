package com.nsbm.health.availability.service.impl;

import com.nsbm.health.availability.dto.AvailabilityResponse;
import com.nsbm.health.availability.dto.CreateAvailabilityRequest;
import com.nsbm.health.availability.exception.BadRequestException;
import com.nsbm.health.availability.exception.ConflictException;
import com.nsbm.health.availability.exception.ResourceNotFoundException;
import com.nsbm.health.availability.model.AvailabilitySlot;
import com.nsbm.health.availability.model.AvailabilityStatus;
import com.nsbm.health.availability.repository.AvailabilityRepository;
import com.nsbm.health.availability.service.AvailabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityServiceImpl.class);

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    /**
     * Atomically books an availability slot if it is still AVAILABLE.
     * Prevents double booking in concurrent scenarios.
     */
    @Override
    public AvailabilityResponse bookAvailability(String availabilityId) {

        if (availabilityId == null || availabilityId.isBlank()) {
            throw new BadRequestException("availabilityId is required");
        }

        log.info("Booking availability slot: {}", availabilityId);

        AvailabilitySlot updated = availabilityRepository
                .bookIfAvailable(availabilityId)
                .orElse(null);

        if (updated != null) {
            log.info("Booked successfully: {}", availabilityId);
            return toResponse(updated);
        }

        // Determine failure reason
        if (!availabilityRepository.existsById(availabilityId)) {
            log.warn("Booking failed - slot not found: {}", availabilityId);
            throw new ResourceNotFoundException("Availability slot not found: " + availabilityId);
        }

        log.warn("Booking failed - already booked: {}", availabilityId);
        throw new ConflictException("Availability slot is already booked");
    }

    @Override
    public AvailabilityResponse createAvailability(CreateAvailabilityRequest request) {

        if (request == null) {
            throw new BadRequestException("request body is required");
        }

        validateTimeRange(request.getStartTime(), request.getEndTime());

        log.info("Creating availability: counselorId={}, date={}, {}-{}",
                request.getCounselorId(), request.getDate(),
                request.getStartTime(), request.getEndTime());

        List<AvailabilitySlot> sameDaySlots =
                availabilityRepository.findByCounselorIdAndDateOrderByStartTimeAsc(
                        request.getCounselorId(),
                        request.getDate()
                );

        for (AvailabilitySlot existing : sameDaySlots) {
            if (overlaps(
                    request.getStartTime(),
                    request.getEndTime(),
                    existing.getStartTime(),
                    existing.getEndTime()
            )) {
                log.warn("Create failed - overlap detected with slotId={}", existing.getAvailabilityId());
                throw new ConflictException(
                        "Overlapping availability slot exists for this counselor on the given date"
                );
            }
        }

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setCounselorId(request.getCounselorId());
        slot.setDate(request.getDate());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(AvailabilityStatus.AVAILABLE);

        AvailabilitySlot saved = availabilityRepository.save(slot);

        log.info("Availability created: availabilityId={}", saved.getAvailabilityId());

        return toResponse(saved);
    }

    @Override
    public List<AvailabilityResponse> getAvailability(String counselorId, LocalDate date) {

        if (counselorId == null || counselorId.isBlank()) {
            throw new BadRequestException("counselorId is required");
        }
        if (date == null) {
            throw new BadRequestException("date is required");
        }

        log.info("Fetching availability: counselorId={}, date={}", counselorId, date);

        return availabilityRepository
                .findByCounselorIdAndDateOrderByStartTimeAsc(counselorId, date)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateTimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new BadRequestException("startTime and endTime are required");
        }
        if (!start.isBefore(end)) {
            throw new BadRequestException("startTime must be before endTime");
        }
    }

    /**
     * Overlap condition:
     * [start, end) intersects [existingStart, existingEnd)
     */
    private boolean overlaps(
            LocalTime start,
            LocalTime end,
            LocalTime existingStart,
            LocalTime existingEnd
    ) {
        return start.isBefore(existingEnd) && end.isAfter(existingStart);
    }

    private AvailabilityResponse toResponse(AvailabilitySlot slot) {
        AvailabilityResponse resp = new AvailabilityResponse();
        resp.setAvailabilityId(slot.getAvailabilityId());
        resp.setCounselorId(slot.getCounselorId());
        resp.setDate(slot.getDate());
        resp.setStartTime(slot.getStartTime());
        resp.setEndTime(slot.getEndTime());
        resp.setStatus(slot.getStatus());
        return resp;
    }
}