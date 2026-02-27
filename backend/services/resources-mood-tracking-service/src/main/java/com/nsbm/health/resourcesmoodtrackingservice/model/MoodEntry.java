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

/**
 * MoodEntry - A record of one mood log entry by a user
 */
@Document(collection = "mood_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A mood entry record for a user")
public class MoodEntry {

    // Unique identifier for this mood entry
    @Id
    @Schema(description = "Unique identifier")
    private String id;

    // Which user this mood belongs to
    @NotBlank(message = "User ID is required")
    @Schema(description = "User ID")
    private String userId;

    // The mood level (1-10 scale, 1=worst, 10=best)
    @NotNull(message = "Mood level is required")
    @Min(value = 1, message = "Mood level must be between 1 and 10")
    @Max(value = 10, message = "Mood level must be between 1 and 10")
    @Schema(description = "Mood level (1-10 scale)", minimum = "1", maximum = "10")
    private Integer moodLevel;

    // The category of mood (happy, sad, anxious, etc)
    @NotBlank(message = "Mood category is required")
    @Schema(description = "Mood category (e.g., happy, sad, anxious)")
    private String moodCategory;

    // Optional notes about why they feel this way
    @Schema(description = "Optional notes about the mood")
    private String notes;

    // When this entry was created
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    // When this entry was last updated
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    // If true, this entry is hidden/deleted
    @Schema(description = "Whether the entry is archived")
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
