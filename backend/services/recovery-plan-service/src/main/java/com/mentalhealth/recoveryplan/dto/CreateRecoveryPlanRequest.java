package com.mentalhealth.recoveryplan.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO for creating a new recovery plan, Used by the counselor to submit a form to create a plan
// @NotBlank and @NotNull ensures the fields arent empty

public class CreateRecoveryPlanRequest {

    @NotBlank(message = "Patient ID is Required")
    private String patientId;

    @NotBlank(message = "Counselor ID is Required")
    private String counselorId;

    private String appointmentId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Start date is Required")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public CreateRecoveryPlanRequest() {
    }

    public CreateRecoveryPlanRequest(String appointmentId, String counselorId, String description, LocalDateTime endDate, String patientId, LocalDateTime startDate, String title) {
        this.appointmentId = appointmentId;
        this.counselorId = counselorId;
        this.description = description;
        this.endDate = endDate;
        this.patientId = patientId;
        this.startDate = startDate;
        this.title = title;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
