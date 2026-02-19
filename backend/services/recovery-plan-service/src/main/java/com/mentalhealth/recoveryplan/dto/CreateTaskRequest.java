package com.mentalhealth.recoveryplan.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO for adding a task to a recovery plan

public class CreateTaskRequest {

    @NotBlank(message = "Task description is Required")
    private String description;

    @NotNull(message = "Due date is Required")
    private LocalDateTime dueDate;

    private String counselorNotes;

    public CreateTaskRequest() {
    }

    public CreateTaskRequest(@NotBlank(message = "Task description is Required") String description,
            @NotNull(message = "Due date is Required") LocalDateTime dueDate, String counselorNotes) {
        this.description = description;
        this.dueDate = dueDate;
        this.counselorNotes = counselorNotes;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public String getCounselorNotes() {
        return counselorNotes;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setCounselorNotes(String counselorNotes) {
        this.counselorNotes = counselorNotes;
    }
}
