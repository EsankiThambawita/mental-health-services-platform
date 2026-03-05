package com.nsbm.health.resourcesmoodtrackingservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;

// Single mood entry record for a user
@Document(collection = "mood_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A mood entry record for a user")
public class MoodEntry {

    // Unique ID
    @Id
    @Schema(description = "Unique identifier")
    private String id;

    // Which user
    @NotBlank(message = "User ID is required")
    @Schema(description = "User ID")
    private String userId;

    // Mood level 1-10
    @NotNull(message = "Mood level is required")
    @Min(value = 1, message = "Mood level must be between 1 and 10")
    @Max(value = 10, message = "Mood level must be between 1 and 10")
    @Schema(description = "Mood level (1-10 scale)", minimum = "1", maximum = "10")
    private Integer moodLevel;

    // Category of mood
    @NotBlank(message = "Mood category is required")
    @Schema(description = "Mood category (e.g., happy, sad, anxious)")
    private String moodCategory;

    // Optional notes
    @Schema(description = "Optional notes about the mood")
    private String notes;

    // When created
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    // When updated
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    // If archived
    @Schema(description = "Whether the entry is archived")
    private Boolean archived = false;

    // Set timestamps before saving
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
