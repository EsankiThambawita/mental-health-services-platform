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

    // Empty const for MongoDB
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

    @Override
    public String toString() {
        return "RecoveryPlan [id=" + id + ", patientId=" + patientId + ", conselorId=" + conselorId + ", appointmentId="
                + appointmentId + ", title=" + title + ", description=" + description + ", startDate=" + startDate
                + ", endDate=" + endDate + ", status=" + status + ", tasks=" + tasks + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((patientId == null) ? 0 : patientId.hashCode());
        result = prime * result + ((conselorId == null) ? 0 : conselorId.hashCode());
        result = prime * result + ((appointmentId == null) ? 0 : appointmentId.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RecoveryPlan other = (RecoveryPlan) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (patientId == null) {
            if (other.patientId != null)
                return false;
        } else if (!patientId.equals(other.patientId))
            return false;
        if (conselorId == null) {
            if (other.conselorId != null)
                return false;
        } else if (!conselorId.equals(other.conselorId))
            return false;
        if (appointmentId == null) {
            if (other.appointmentId != null)
                return false;
        } else if (!appointmentId.equals(other.appointmentId))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (status != other.status)
            return false;
        if (tasks == null) {
            if (other.tasks != null)
                return false;
        } else if (!tasks.equals(other.tasks))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        if (updatedAt == null) {
            if (other.updatedAt != null)
                return false;
        } else if (!updatedAt.equals(other.updatedAt))
            return false;
        return true;
    }

    
}
