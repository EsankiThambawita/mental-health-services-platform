package com.nsbm.health.counselor.controller;

import com.nsbm.health.counselor.dto.*;
import com.nsbm.health.counselor.service.CounselorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API endpoints
 */
@RestController
@RequestMapping("/api/counselors")
public class CounselorController {

    private final CounselorService service;

    public CounselorController(CounselorService service) {
        this.service = service;
    }

    @PostMapping
    public CounselorResponseDTO create(@RequestBody CounselorRequestDTO dto) {
        return service.createCounselor(dto);
    }

    @PutMapping("/{id}")
    public CounselorResponseDTO update(@PathVariable String id,
                                       @RequestBody CounselorRequestDTO dto) {
        return service.updateCounselor(id, dto);
    }

    @GetMapping
    public List<CounselorResponseDTO> getAll() {
        return service.getAllCounselors();
    }

    @GetMapping("/{id}")
    public CounselorResponseDTO getById(@PathVariable String id) {
        return service.getCounselorById(id);
    }
}
