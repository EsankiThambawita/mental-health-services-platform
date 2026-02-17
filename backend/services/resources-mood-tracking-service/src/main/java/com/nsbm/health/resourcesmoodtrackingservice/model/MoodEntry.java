package com.nsbm.health.resourcesmoodtrackingservice.model;

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

/**
 * MoodEntry - A record of one mood log entry by a user
 */
@Document(collection = "mood_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoodEntry {

    // Unique identifier for this mood entry
    @Id
    private String id;

    // Which user this mood belongs to
    @NotBlank(message = "User ID is required")
    private String userId;

    // The mood level (1-10 scale, 1=worst, 10=best)
    @NotNull(message = "Mood level is required")
    @Min(value = 1, message = "Mood level must be between 1 and 10")
    @Max(value = 10, message = "Mood level must be between 1 and 10")
    private Integer moodLevel;

    // The category of mood (happy, sad, anxious, etc)
    @NotBlank(message = "Mood category is required")
    private String moodCategory;

    // Optional notes about why they feel this way
    private String notes;

    // When this entry was created
    private LocalDateTime createdAt;

    // When this entry was last updated
    private LocalDateTime updatedAt;

    // If true, this entry is hidden/deleted
    private Boolean archived = false;

    /**
     * Set timestamps before saving to database
     */
    public void prePersist() {
        // Set creation time only if not already set
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        // Always update the modified time
        this.updatedAt = LocalDateTime.now();
    }
}
