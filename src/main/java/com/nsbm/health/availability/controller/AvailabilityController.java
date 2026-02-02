package com.nsbm.health.availability.controller;

import com.nsbm.health.availability.dto.AvailabilitySlotDTO;
import com.nsbm.health.availability.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    private final AvailabilityService service;

    @Autowired
    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AvailabilitySlotDTO> createSlot(@RequestBody AvailabilitySlotDTO dto) {
        return ResponseEntity.ok(service.createSlot(dto));
    }

    @GetMapping
    public ResponseEntity<List<AvailabilitySlotDTO>> getAllSlots() {
        return ResponseEntity.ok(service.getAllSlots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilitySlotDTO> getSlotById(@PathVariable String id) {
        AvailabilitySlotDTO dto = service.getSlotById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilitySlotDTO> updateSlot(@PathVariable String id,
                                                          @RequestBody AvailabilitySlotDTO dto) {
        AvailabilitySlotDTO updated = service.updateSlot(id, dto);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable String id) {
        service.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<List<AvailabilitySlotDTO>> getSlotsByCounselor(@PathVariable String counselorId) {
        return ResponseEntity.ok(service.getSlotsByCounselor(counselorId));
    }

    @GetMapping("/counselor/{counselorId}/available")
    public ResponseEntity<List<AvailabilitySlotDTO>> getAvailableSlots(@PathVariable String counselorId) {
        return ResponseEntity.ok(service.getAvailableSlotsByCounselor(counselorId));
    }
}
