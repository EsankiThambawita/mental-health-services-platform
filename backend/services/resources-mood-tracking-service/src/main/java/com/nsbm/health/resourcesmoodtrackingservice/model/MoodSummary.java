package com.nsbm.health.resourcesmoodtrackingservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * MoodSummary represents aggregated mood analytics for a user
 */
@Document(collection = "mood_summaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Aggregated mood analytics for a user")
public class MoodSummary {

    @Id
    @Schema(description = "Unique identifier")
    private String id;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Period type: DAILY, WEEKLY, MONTHLY")
    private String period;

    @Schema(description = "Period start timestamp")
    private LocalDateTime periodStart;

    @Schema(description = "Period end timestamp")
    private LocalDateTime periodEnd;

    @Schema(description = "Average mood for the period")
    private Double averageMood;

    @Schema(description = "Total number of entries in period")
    private Integer totalEntries;

    @Schema(description = "Highest mood level in period")
    private Integer highestMood;

    @Schema(description = "Lowest mood level in period")
    private Integer lowestMood;

    @Schema(description = "Frequency of each mood category")
    private Map<String, Integer> moodCategoryFrequency;

    @Schema(description = "Most frequently recorded mood category")
    private String dominantMood;

    @Schema(description = "Percentage change from previous period")
    private Double trendPercentage;

    @Schema(description = "Timestamp when summary was generated")
    private LocalDateTime generatedAt;

    public void prePersist() {
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
        }
    }
}

