package com.nsbm.health.availability.service;

import com.nsbm.health.availability.dto.AvailabilityResponse;
import com.nsbm.health.availability.dto.CreateAvailabilityRequest;
import com.nsbm.health.availability.exception.BadRequestException;
import com.nsbm.health.availability.exception.ConflictException;
import com.nsbm.health.availability.exception.ResourceNotFoundException;
import com.nsbm.health.availability.model.AvailabilitySlot;
import com.nsbm.health.availability.model.AvailabilityStatus;
import com.nsbm.health.availability.repository.AvailabilityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public AvailabilityResponse createAvailability(CreateAvailabilityRequest request) {
        validateTimeRange(request.getStartTime(), request.getEndTime());

        // Load same-counselor same-day slots and ensure no overlap.
        // Overlap rule: [start, end) intersects [existingStart, existingEnd)
        // i.e., start < existingEnd AND end > existingStart
        List<AvailabilitySlot> sameDaySlots =
                availabilityRepository.findByCounselorIdAndDateOrderByStartTimeAsc(request.getCounselorId(), request.getDate());

        for (AvailabilitySlot existing : sameDaySlots) {
            if (overlaps(request.getStartTime(), request.getEndTime(), existing.getStartTime(), existing.getEndTime())) {
                throw new ConflictException("Overlapping availability slot exists for this counselor on the given date");
            }
        }

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setCounselorId(request.getCounselorId());
        slot.setDate(request.getDate());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(AvailabilityStatus.AVAILABLE);

        AvailabilitySlot saved = availabilityRepository.save(slot);
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

        return availabilityRepository.findByCounselorIdAndDateOrderByStartTimeAsc(counselorId, date)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AvailabilityResponse bookAvailability(String availabilityId) {
        AvailabilitySlot slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found: " + availabilityId));

        if (slot.getStatus() == AvailabilityStatus.BOOKED) {
            throw new ConflictException("Availability slot is already booked");
        }

        slot.setStatus(AvailabilityStatus.BOOKED);
        AvailabilitySlot saved = availabilityRepository.save(slot);
        return toResponse(saved);
    }

    private void validateTimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new BadRequestException("startTime and endTime are required");
        }
        if (!start.isBefore(end)) {
            throw new BadRequestException("startTime must be before endTime");
        }
    }

    private boolean overlaps(LocalTime start, LocalTime end, LocalTime existingStart, LocalTime existingEnd) {
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
