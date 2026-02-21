package com.mentalhealth.recoveryplan.dto;

import com.mentalhealth.recoveryplan.model.PlanStatus;

import jakarta.validation.constraints.NotNull;

// DTO for updating a plans status

public class UpdatePlanStatusRequest {

    @NotNull(message = "Status is required")
    private PlanStatus status;

    public UpdatePlanStatusRequest() {
    }

    public UpdatePlanStatusRequest(@NotNull(message = "Status is required") PlanStatus status) {
        this.status = status;
    }

    public PlanStatus getStatus() {
        return status;
    }

    public void setStatus(PlanStatus status) {
        this.status = status;
    }
}
