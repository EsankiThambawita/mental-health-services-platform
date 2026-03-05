package com.mentalhealth.recoveryplan.dto;

// DTO for returning task info

import java.time.LocalDateTime;


public class TaskResponse {

    private String taskId;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    private LocalDateTime completedAt;
    private String counselorNotes;
    private LocalDateTime createdAt;

    public TaskResponse() {
    }

    public TaskResponse(String taskId, String description, LocalDateTime dueDate, boolean completed,
            LocalDateTime completedAt, String counselorNotes, LocalDateTime createdAt) {
        this.taskId = taskId;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.completedAt = completedAt;
        this.counselorNotes = counselorNotes;
        this.createdAt = createdAt;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getCounselorNotes() {
        return counselorNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setCounselorNotes(String counselorNotes) {
        this.counselorNotes = counselorNotes;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
