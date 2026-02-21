package com.nsbm.health.availability.service;

import com.nsbm.health.availability.dto.AvailabilitySlotDTO;
import com.nsbm.health.availability.entity.AvailabilitySlot;
import com.nsbm.health.availability.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final AvailabilityRepository repository;

    @Autowired
    public AvailabilityService(AvailabilityRepository repository) {
        this.repository = repository;
    }

    // Create
    public AvailabilitySlotDTO createSlot(AvailabilitySlotDTO dto) {
        AvailabilitySlot slot = new AvailabilitySlot(
                null,
                dto.getCounselorId(),
                dto.getTitle(),
                dto.getDate(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getIsBooked()
        );
        AvailabilitySlot saved = repository.save(slot);
        return mapToDTO(saved);
    }

    // Read All
    public List<AvailabilitySlotDTO> getAllSlots() {
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Read by ID
    public AvailabilitySlotDTO getSlotById(String id) {
        return repository.findById(id).map(this::mapToDTO).orElse(null);
    }

    // Update
    public AvailabilitySlotDTO updateSlot(String id, AvailabilitySlotDTO dto) {
        return repository.findById(id).map(slot -> {
            slot.setTitle(dto.getTitle());
            slot.setDate(dto.getDate());
            slot.setStartTime(dto.getStartTime());
            slot.setEndTime(dto.getEndTime());
            slot.setIsBooked(dto.getIsBooked());
            return mapToDTO(repository.save(slot));
        }).orElse(null);
    }

    // Delete
    public void deleteSlot(String id) {
        repository.deleteById(id);
    }

    // Get slots by counselor
    public List<AvailabilitySlotDTO> getSlotsByCounselor(String counselorId) {
        return repository.findByCounselorId(counselorId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get available slots by counselor
    public List<AvailabilitySlotDTO> getAvailableSlotsByCounselor(String counselorId) {
        return repository.findByCounselorIdAndIsBookedFalse(counselorId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Mapper
    private AvailabilitySlotDTO mapToDTO(AvailabilitySlot slot) {
        return new AvailabilitySlotDTO(
                slot.getId(),
                slot.getCounselorId(),
                slot.getTitle(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getIsBooked()
        );
    }
}
