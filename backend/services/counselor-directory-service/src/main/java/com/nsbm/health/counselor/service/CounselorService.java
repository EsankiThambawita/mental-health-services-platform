package com.nsbm.health.counselor.service;


import com.nsbm.health.counselor.dto.CounselorRequestDTO;
import com.nsbm.health.counselor.dto.CounselorResponseDTO;
import java.util.List;

public interface CounselorService {

    CounselorResponseDTO createCounselor(CounselorRequestDTO dto);

    CounselorResponseDTO updateCounselor(String id, CounselorRequestDTO dto);

    List<CounselorResponseDTO> getAllCounselors();

    CounselorResponseDTO getCounselorById(String id);
}

