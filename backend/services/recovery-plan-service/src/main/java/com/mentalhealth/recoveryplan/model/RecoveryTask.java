package com.mentalhealth.recoveryplan.model;
import java.time.LocalDateTime;


// RecoveryTask - An embedded document within RecoveryPlan
public class RecoveryTask {
    
    private String taskID;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    private LocalDateTime completedAt;
    private String counselorNotes; // Optional: Notes from counselor about this specific task
    private LocalDateTime createdAt;

    // For MongoDB
    public RecoveryTask() {
    }

    public RecoveryTask(String taskID, String description, LocalDateTime dueDate, String counselorNotes) {
        this.taskID = taskID;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
        this.completedAt = null;
        this.counselorNotes = counselorNotes;
        this.createdAt = LocalDateTime.now();
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getCounselorNotes() {
        return counselorNotes;
    }

    public void setCounselorNotes(String counselorNotes) {
        this.counselorNotes = counselorNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
}
