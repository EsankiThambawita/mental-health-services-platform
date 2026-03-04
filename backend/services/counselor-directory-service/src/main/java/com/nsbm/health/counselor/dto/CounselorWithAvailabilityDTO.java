package com.nsbm.health.counselor.dto;

import com.nsbm.health.counselor.dto.availability.AvailabilityResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CounselorWithAvailabilityDTO {
    private CounselorResponseDTO counselor;
    private List<AvailabilityResponse> availability;
}