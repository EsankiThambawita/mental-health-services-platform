package com.mentalhealth.recoveryplan.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// RecoveryPlan - Main entity representing a patient's recovery plan
@Document(collection = "recovery_plan")
public class RecoveryPlan {

    // MongoDB document ID
    @Id
    private String id;

    private String patientId;

    private String conselorId;

    private String appointmentId;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private PlanStatus status;

    private List<RecoveryTask> tasks = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Empty constructor for MongoDB
    public RecoveryPlan() {
    }

    public RecoveryPlan(String patientId, String conselorId, String title, String description, LocalDateTime startDate,
            LocalDateTime endDate) {
        this.patientId = patientId;
        this.conselorId = conselorId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = PlanStatus.ACTIVE; // New plans are always ACTIVE
        this.tasks = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getConselorId() {
        return conselorId;
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

    public List<RecoveryTask> getTasks() {
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

    public void setConselorId(String conselorId) {
        this.conselorId = conselorId;
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

    public void setTasks(List<RecoveryTask> tasks) {
        this.tasks = tasks;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
