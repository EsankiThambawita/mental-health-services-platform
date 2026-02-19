package com.nsbm.health.counselor.util;

import com.nsbm.health.counselor.dto.*;
import com.nsbm.health.counselor.model.CounselorProfile;

/**
 * Converts between Entity and DTO
 */
public class MapperUtil {

    public static CounselorProfile toEntity(CounselorRequestDTO dto) {
        CounselorProfile profile = new CounselorProfile();
        profile.setName(dto.getName());
        profile.setLanguages(dto.getLanguages());
        profile.setSpecializations(dto.getSpecializations());
        profile.setExperienceYears(dto.getExperienceYears());
        return profile;
    }

    public static CounselorResponseDTO toDTO(CounselorProfile profile) {
        CounselorResponseDTO dto = new CounselorResponseDTO();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setLanguages(profile.getLanguages());
        dto.setSpecializations(profile.getSpecializations());
        dto.setExperienceYears(profile.getExperienceYears());
        return dto;
    }
}
