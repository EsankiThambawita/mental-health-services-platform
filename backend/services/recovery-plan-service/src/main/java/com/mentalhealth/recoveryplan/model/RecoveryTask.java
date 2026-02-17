package com.mentalhealth.recoveryplan.model;
import java.time.LocalDateTime;


// RecoveryTask - An embedded document within RecoveryPlan
public class RecoveryTask {
    
    private String taskID;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    private LocalDateTime completedAt;
    private String conselorNotes; // Optional: Notes from counselor about this specific task
    private LocalDateTime createdAt;

    public RecoveryTask(String taskID, String description, LocalDateTime dueDate, String conselorNotes) {
        this.taskID = taskID;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
        this.completedAt = null;
        this.conselorNotes = conselorNotes;
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

    public String getConselorNotes() {
        return conselorNotes;
    }

    public void setConselorNotes(String conselorNotes) {
        this.conselorNotes = conselorNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
}
