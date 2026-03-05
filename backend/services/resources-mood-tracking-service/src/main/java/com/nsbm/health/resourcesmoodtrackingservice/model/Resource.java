package com.nsbm.health.resourcesmoodtrackingservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

// Mental health resource (article, exercise, etc)
@Document(collection = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mental health resource entity")
public class Resource {

    // Unique ID
    @Id
    @Schema(description = "Unique identifier")
    private String id;

    // Title/name
    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the resource")
    private String title;

    // Short summary
    @NotBlank(message = "Description is required")
    @Schema(description = "Short description of the resource")
    private String description;

    // Full content
    @Schema(description = "Full content of the resource")
    private String content;

    // Category
    @NotBlank(message = "Category is required")
    @Schema(description = "Main category of the resource")
    private String category;

    // Type: ARTICLE, EXERCISE, TECHNIQUE, CRISIS_INFO
    @Schema(description = "Type of resource (ARTICLE, EXERCISE, CRISIS_INFO, TECHNIQUE)")
    private String resourceType;

    // Tags
    @Schema(description = "Tags for searching the resource")
    private List<String> tags;

    // Mood levels (1-10 scale)
    @Schema(description = "Recommended mood levels (1-10 scale)")
    private List<Integer> recommendedMoodLevels;

    // Difficulty: EASY, MEDIUM, HARD
    @Schema(description = "Difficulty level (EASY, MEDIUM, HARD)")
    private String difficulty;

    // Duration in minutes
    @Schema(description = "Duration in minutes")
    private Integer durationMinutes;

    // Source URL
    @Schema(description = "Source URL")
    private String sourceUrl;

    // Author
    @Schema(description = "Author of the resource")
    private String author;

    // Is visible?
    @Schema(description = "Whether the resource is active")
    private Boolean isActive = true;

    // View count tracker
    @Schema(description = "View count")
    private Integer viewCount = 0;

    // Timestamps
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Publication timestamp")
    private LocalDateTime publishedAt;

    // Set timestamps before saving
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
}

