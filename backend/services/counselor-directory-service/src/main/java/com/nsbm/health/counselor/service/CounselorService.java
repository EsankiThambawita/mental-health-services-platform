package com.nsbm.health.counselor.service;

import com.nsbm.health.counselor.dto.CounselorRequestDTO;
import com.nsbm.health.counselor.dto.CounselorResponseDTO;

import java.util.List;

/**
 * Service interface for counselor operations
 */
public interface CounselorService {

    CounselorResponseDTO createCounselor(CounselorRequestDTO dto);

    CounselorResponseDTO updateCounselor(String id, CounselorRequestDTO dto);

    List<CounselorResponseDTO> getAllCounselors();

    CounselorResponseDTO getCounselorById(String id);

    List<CounselorResponseDTO> searchCounselors(String query);

    void deleteCounselor(String id);
}