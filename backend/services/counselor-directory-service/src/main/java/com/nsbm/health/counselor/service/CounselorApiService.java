package com.nsbm.health.counselor.service;

import com.nsbm.health.counselor.dto.CounselorRequestDTO;
import com.nsbm.health.counselor.dto.CounselorResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Central internal API service for counselor operations.
 */
@Service
public class CounselorApiService {

    private final CounselorService counselorService;

    public CounselorApiService(CounselorService counselorService) {
        this.counselorService = counselorService;
    }

    public List<CounselorResponseDTO> getAllCounselors() {
        return counselorService.getAllCounselors();
    }

    public CounselorResponseDTO addCounselor(CounselorRequestDTO dto) {
        return counselorService.createCounselor(dto);
    }

    public List<CounselorResponseDTO> searchCounselors(String query) {
        return counselorService.searchCounselors(query);
    }

    public CounselorResponseDTO updateCounselor(String id, CounselorRequestDTO dto) {
        return counselorService.updateCounselor(id, dto);
    }

    public CounselorResponseDTO getCounselorById(String id) {
        return counselorService.getCounselorById(id);
    }
}