package com.nsbm.health.counselor.client;

import com.nsbm.health.counselor.dto.availability.AvailabilityResponse;
import com.nsbm.health.counselor.dto.availability.CreateAvailabilityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "availability-management-service",
        url = "${availability.service.url:http://localhost:8082}")
public interface AvailabilityClient {

    @GetMapping("/api/availability/counselor/{counselorId}")
    List<AvailabilityResponse> getCounselorAvailability(@PathVariable("counselorId") String counselorId);

    @GetMapping("/api/availability/counselor/{counselorId}/date/{date}")
    List<AvailabilityResponse> getAvailabilityByDate(
            @PathVariable("counselorId") String counselorId,
            @PathVariable("date") String date);

    @GetMapping("/api/availability/{availabilityId}")
    AvailabilityResponse getAvailabilityById(@PathVariable("availabilityId") String availabilityId);

    @GetMapping("/api/availability/check")
    Boolean checkAvailability(
            @RequestParam("counselorId") String counselorId,
            @RequestParam("date") String date,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime);

    @PostMapping("/api/availability")
    AvailabilityResponse createAvailability(@RequestBody CreateAvailabilityRequest request);

    @PutMapping("/api/availability/{availabilityId}")
    AvailabilityResponse updateAvailability(
            @PathVariable("availabilityId") String availabilityId,
            @RequestBody CreateAvailabilityRequest request);

    @DeleteMapping("/api/availability/{availabilityId}")
    void deleteAvailability(@PathVariable("availabilityId") String availabilityId);
}