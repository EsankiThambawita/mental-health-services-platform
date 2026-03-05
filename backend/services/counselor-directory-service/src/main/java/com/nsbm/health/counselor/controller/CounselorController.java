package com.nsbm.health.counselor.controller;

import com.nsbm.health.counselor.dto.*;
import com.nsbm.health.counselor.dto.availability.AvailabilityResponse;
import com.nsbm.health.counselor.service.CounselorApiService;
import com.nsbm.health.counselor.service.CounselorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * REST API endpoints
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/counselors")
public class CounselorController {

    private final CounselorService service;
    private final CounselorApiService apiService;

    public CounselorController(CounselorService service, CounselorApiService apiService) {
        this.service = service;
        this.apiService = apiService;
    }

    @PostMapping
    public CounselorResponseDTO create(@RequestBody CounselorRequestDTO dto) {
        return service.createCounselor(dto);
    }

    @PutMapping("/{id}")
    public CounselorResponseDTO update(@PathVariable String id,
                                       @RequestBody CounselorRequestDTO dto) {
        return service.updateCounselor(id, dto);
    }

    @GetMapping
    public List<CounselorResponseDTO> getAll() {
        return service.getAllCounselors();
    }

    @GetMapping("/{id}")
    public CounselorResponseDTO getById(@PathVariable String id) {
        return service.getCounselorById(id);
    }

    @GetMapping("/search")
    public List<CounselorResponseDTO> search(@RequestParam String query) {
        return service.searchCounselors(query);
    }

    // ==================== NEW AVAILABILITY ENDPOINTS ====================

    @GetMapping("/{counselorId}/availability")
    public List<AvailabilityResponse> getCounselorAvailability(
            @PathVariable String counselorId) {
        return apiService.getCounselorAvailability(counselorId);
    }

    @GetMapping("/{counselorId}/availability/date/{date}")
    public List<AvailabilityResponse> getAvailabilityByDate(
            @PathVariable String counselorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return apiService.getCounselorAvailabilityByDate(counselorId, date);
    }

    @GetMapping("/{counselorId}/availability/check")
    public Boolean checkAvailability(
            @PathVariable String counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return apiService.isCounselorAvailable(counselorId, date, startTime, endTime);
    }

    @PostMapping("/{counselorId}/availability")
    public AvailabilityResponse createAvailability(
            @PathVariable String counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return apiService.createCounselorAvailability(counselorId, date, startTime, endTime);
    }

    @PutMapping("/availability/{availabilityId}")
    public AvailabilityResponse updateAvailability(
            @PathVariable String availabilityId,
            @RequestParam String counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return apiService.updateCounselorAvailability(availabilityId, counselorId, date, startTime, endTime);
    }

    @DeleteMapping("/availability/{availabilityId}")
    public void deleteAvailability(@PathVariable String availabilityId) {
        apiService.deleteCounselorAvailability(availabilityId);
    }

    @GetMapping("/{counselorId}/with-availability")
    public CounselorWithAvailabilityDTO getCounselorWithAvailability(
            @PathVariable String counselorId) {
        return apiService.getCounselorWithAvailability(counselorId);
    }
    @DeleteMapping("/{id}")
    public void deleteCounselor(@PathVariable String id) {
        service.deleteCounselor(id);
    }
}
