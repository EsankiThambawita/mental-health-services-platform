package com.nsbm.health.counselor.service;

import com.nsbm.health.counselor.client.AvailabilityClient;
import com.nsbm.health.counselor.dto.CounselorRequestDTO;
import com.nsbm.health.counselor.dto.CounselorResponseDTO;
import com.nsbm.health.counselor.dto.CounselorWithAvailabilityDTO;
import com.nsbm.health.counselor.dto.availability.AvailabilityResponse;
import com.nsbm.health.counselor.dto.availability.CreateAvailabilityRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CounselorApiService implements CounselorService {

    private final CounselorService counselorService;
    private final AvailabilityClient availabilityClient;

    public CounselorApiService(CounselorService counselorService, AvailabilityClient availabilityClient) {
        this.counselorService = counselorService;
        this.availabilityClient = availabilityClient;
    }

    // ==================== CounselorService Interface Methods ====================

    @Override
    public CounselorResponseDTO createCounselor(CounselorRequestDTO dto) {
        return counselorService.createCounselor(dto);
    }

    @Override
    public CounselorResponseDTO updateCounselor(String id, CounselorRequestDTO dto) {
        return counselorService.updateCounselor(id, dto);
    }

    @Override
    public List<CounselorResponseDTO> getAllCounselors() {
        return counselorService.getAllCounselors();
    }

    @Override
    public CounselorResponseDTO getCounselorById(String id) {
        return counselorService.getCounselorById(id);
    }

    @Override
    public List<CounselorResponseDTO> searchCounselors(String query) {
        return counselorService.searchCounselors(query);
    }

    // ==================== NEW Availability Methods ====================

    public List<AvailabilityResponse> getCounselorAvailability(String counselorId) {
        return availabilityClient.getCounselorAvailability(counselorId);
    }

    public List<AvailabilityResponse> getCounselorAvailabilityByDate(
            String counselorId,
            LocalDate date) {
        return availabilityClient.getAvailabilityByDate(counselorId, date.toString());
    }

    public Boolean isCounselorAvailable(
            String counselorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        return availabilityClient.checkAvailability(
                counselorId,
                date.toString(),
                startTime.toString(),
                endTime.toString()
        );
    }

    public AvailabilityResponse createCounselorAvailability(
            String counselorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setCounselorId(counselorId);
        request.setDate(date);
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        return availabilityClient.createAvailability(request);
    }

    public AvailabilityResponse updateCounselorAvailability(
            String availabilityId,
            String counselorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setCounselorId(counselorId);
        request.setDate(date);
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        return availabilityClient.updateAvailability(availabilityId, request);
    }

    public void deleteCounselorAvailability(String availabilityId) {
        availabilityClient.deleteAvailability(availabilityId);
    }

    public CounselorWithAvailabilityDTO getCounselorWithAvailability(String counselorId) {
        CounselorResponseDTO counselor = getCounselorById(counselorId);
        List<AvailabilityResponse> availability = getCounselorAvailability(counselorId);

        return new CounselorWithAvailabilityDTO(counselor, availability);
    }
}