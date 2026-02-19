package com.mentalhealth.recoveryplan.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mentalhealth.recoveryplan.model.PlanStatus;

// DTO for returning recovery plan info

public class RecoveryPlanResponse {

    private String id;
    private String patientId;
    private String counselorId;
    private String appointmentId;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PlanStatus status;
    private List<TaskResponse> tasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecoveryPlanResponse() {
    }

    public RecoveryPlanResponse(String id, String patientId, String counselorId, String appointmentId, String title,
            String description, LocalDateTime startDate, LocalDateTime endDate, PlanStatus status,
            List<TaskResponse> tasks, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.counselorId = counselorId;
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.tasks = tasks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
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

    public PlanStatus getStatus() {
        return status;
    }

    public List<TaskResponse> getTasks() {
        return tasks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setStatus(PlanStatus status) {
        this.status = status;
    }

    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
