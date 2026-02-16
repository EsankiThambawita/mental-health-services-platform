package com.nsbm.health.counselor.service.impl;

import com.nsbm.health.counselor.dto.*;
import com.nsbm.health.counselor.model.CounselorProfile;
import com.nsbm.health.counselor.repository.CounselorProfileRepository;
import com.nsbm.health.counselor.service.CounselorDirectoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CounselorDirectoryServiceImpl
        implements CounselorDirectoryService {

    private final CounselorProfileRepository repository;

    public CounselorDirectoryServiceImpl(
            CounselorProfileRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public CounselorProfileResponseDTO createCounselor(
            CounselorProfileRequestDTO dto
    ) {
        CounselorProfile counselor = mapToEntity(dto);
        CounselorProfile saved = repository.save(counselor);
        return mapToResponse(saved);
    }

    @Override
    public CounselorProfileResponseDTO updateCounselor(
            String id,
            CounselorProfileRequestDTO dto
    ) {
        CounselorProfile counselor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Counselor not found"));

        counselor.setName(dto.getName());
        counselor.setSpecialization(dto.getSpecialization());
        counselor.setLanguage(dto.getLanguage());
        counselor.setExperienceYears(dto.getExperienceYears());
        counselor.setAvailabilitySummary(dto.getAvailabilitySummary());

        return mapToResponse(repository.save(counselor));
    }

    @Override
    public CounselorProfileResponseDTO getCounselorById(String id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Counselor not found"));
    }

    @Override
    public List<CounselorProfileResponseDTO> getCounselors(
            String specialization,
            String language
    ) {
        List<CounselorProfile> counselors;

        if (specialization != null && language != null) {
            counselors = repository
                    .findBySpecializationAndLanguage(specialization, language);
        } else if (specialization != null) {
            counselors = repository.findBySpecialization(specialization);
        } else if (language != null) {
            counselors = repository.findByLanguage(language);
        } else {
            counselors = repository.findAll();
        }

        return counselors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------- MAPPERS ----------------

    private CounselorProfile mapToEntity(
            CounselorProfileRequestDTO dto
    ) {
        CounselorProfile c = new CounselorProfile();
        c.setName(dto.getName());
        c.setSpecialization(dto.getSpecialization());
        c.setLanguage(dto.getLanguage());
        c.setExperienceYears(dto.getExperienceYears());
        c.setAvailabilitySummary(dto.getAvailabilitySummary());
        return c;
    }

    private CounselorProfileResponseDTO mapToResponse(
            CounselorProfile c
    ) {
        CounselorProfileResponseDTO dto =
                new CounselorProfileResponseDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setSpecialization(c.getSpecialization());
        dto.setLanguage(c.getLanguage());
        dto.setExperienceYears(c.getExperienceYears());
        dto.setAvailabilitySummary(c.getAvailabilitySummary());
        return dto;
    }
}