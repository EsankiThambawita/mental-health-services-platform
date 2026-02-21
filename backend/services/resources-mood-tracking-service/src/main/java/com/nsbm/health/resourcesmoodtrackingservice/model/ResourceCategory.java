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

/**
 * ResourceCategory represents categories for organizing resources
 */
@Document(collection = "resource_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resource category for organizing mental health resources")
public class ResourceCategory {

    @Id
    @Schema(description = "Unique identifier")
    private String id;

    @NotBlank(message = "Category name is required")
    @Schema(description = "Category name")
    private String name;

    @Schema(description = "Category description")
    private String description;

    @Schema(description = "Icon for the category")
    private String icon;

    @Schema(description = "Color code for the category")
    private String color;

    @Schema(description = "Display order in UI")
    private Integer displayOrder;

    @Schema(description = "Tags associated with category")
    private List<String> tags;

    @Schema(description = "Whether category is active")
    private Boolean isActive = true;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
}

