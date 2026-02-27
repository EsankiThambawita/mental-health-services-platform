package com.nsbm.health.counselor.service.impl;
import com.nsbm.health.counselor.util.MapperUtil;
import com.nsbm.health.counselor.dto.*;
import com.nsbm.health.counselor.model.CounselorProfile;
import com.nsbm.health.counselor.repository.CounselorRepository;
import com.nsbm.health.counselor.service.CounselorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic lives here
 */
@Service
public class CounselorServiceImpl implements CounselorService {

    private final CounselorRepository repository;

    public CounselorServiceImpl(CounselorRepository repository) {
        this.repository = repository;
    }

    @Override
    public CounselorResponseDTO createCounselor(CounselorRequestDTO dto) {
        CounselorProfile profile = MapperUtil.toEntity(dto);
        return MapperUtil.toDTO(repository.save(profile));
    }

    @Override
    public CounselorResponseDTO updateCounselor(String id, CounselorRequestDTO dto) {
        CounselorProfile existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Counselor not found"));

        existing.setName(dto.getName());
        existing.setLanguages(dto.getLanguages());
        existing.setSpecializations(dto.getSpecializations());
        existing.setExperienceYears(dto.getExperienceYears());

        return MapperUtil.toDTO(repository.save(existing));
    }

    @Override
    public List<CounselorResponseDTO> getAllCounselors() {
        return repository.findAll()
                .stream()
                .map(MapperUtil::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CounselorResponseDTO getCounselorById(String id) {
        return MapperUtil.toDTO(
                repository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Counselor not found"))
        );
    }
}

