package com.nsbm.health.counselor.service;

import com.nsbm.health.counselor.dto.*;

import java.util.List;

public interface CounselorDirectoryService {

    CounselorProfileResponseDTO createCounselor(
            CounselorProfileRequestDTO dto
    );

    CounselorProfileResponseDTO updateCounselor(
            String id,
            CounselorProfileRequestDTO dto
    );

    CounselorProfileResponseDTO getCounselorById(String id);

    List<CounselorProfileResponseDTO> getCounselors(
            String specialization,
            String language
    );
}