package com.nsbm.health.counselor.controller;

import com.nsbm.health.counselor.dto.*;
import com.nsbm.health.counselor.service.CounselorDirectoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counselors")
public class CounselorDirectoryController {

    private final CounselorDirectoryService service;

    public CounselorDirectoryController(
            CounselorDirectoryService service
    ) {
        this.service = service;
    }

    @PostMapping
    public CounselorProfileResponseDTO createCounselor(
            @RequestBody CounselorProfileRequestDTO dto
    ) {
        return service.createCounselor(dto);
    }

    @PutMapping("/{id}")
    public CounselorProfileResponseDTO updateCounselor(
            @PathVariable String id,
            @RequestBody CounselorProfileRequestDTO dto
    ) {
        return service.updateCounselor(id, dto);
    }

    @GetMapping("/{id}")
    public CounselorProfileResponseDTO getCounselorById(
            @PathVariable String id
    ) {
        return service.getCounselorById(id);
    }

    @GetMapping
    public List<CounselorProfileResponseDTO> getCounselors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String language
    ) {
        return service.getCounselors(specialization, language);
    }
}