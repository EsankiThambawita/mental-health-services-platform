package com.nsbm.health.resourcesmoodtrackingservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource - A mental health support resource (article, exercise, crisis info, etc)
 */
@Document(collection = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mental health resource entity")
public class Resource {

    // Unique identifier for this resource
    @Id
    @Schema(description = "Unique identifier")
    private String id;

    // The name of the resource
    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the resource")
    private String title;

    // Short summary of what the resource is about
    @NotBlank(message = "Description is required")
    @Schema(description = "Short description of the resource")
    private String description;

    // Full content of the resource
    @Schema(description = "Full content of the resource")
    private String content;

    // Main category (relaxation, coping, education, etc)
    @NotBlank(message = "Category is required")
    @Schema(description = "Main category of the resource")
    private String category;

    // Type of resource: ARTICLE, EXERCISE, CRISIS_INFO, or TECHNIQUE
    @Schema(description = "Type of resource (ARTICLE, EXERCISE, CRISIS_INFO, TECHNIQUE)")
    private String resourceType;

    // Tags to help search (meditation, anxiety, breathing, etc)
    @Schema(description = "Tags for searching the resource")
    private List<String> tags;

    // Which mood levels this resource is good for (1-10 scale)
    @Schema(description = "Recommended mood levels (1-10 scale)")
    private List<Integer> recommendedMoodLevels;

    // How hard the resource is: EASY, MEDIUM, or HARD
    @Schema(description = "Difficulty level (EASY, MEDIUM, HARD)")
    private String difficulty;

    // How long it takes to do this resource (in minutes)
    @Schema(description = "Duration in minutes")
    private Integer durationMinutes;

    // Link to the original source if applicable
    @Schema(description = "Source URL")
    private String sourceUrl;

    // Who created this resource
    @Schema(description = "Author of the resource")
    private String author;

    // Whether this resource is visible to users
    @Schema(description = "Whether the resource is active")
    private Boolean isActive = true;

    // How many times this resource has been viewed
    @Schema(description = "View count")
    private Integer viewCount = 0;

    // When this resource was created
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    // When this resource was last updated
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    // When this resource was published
    @Schema(description = "Publication timestamp")
    private LocalDateTime publishedAt;

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

