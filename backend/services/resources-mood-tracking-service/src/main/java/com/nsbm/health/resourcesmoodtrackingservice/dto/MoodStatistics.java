package com.nsbm.health.resourcesmoodtrackingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

// Stats about a user's mood entries
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Statistics about a user's mood entries")
public class MoodStatistics {

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Total number of mood entries")
    private long totalEntries;

    @Schema(description = "Average mood level (1-10)")
    private double averageMood;

    @Schema(description = "Highest mood level recorded")
    private int highestMood;

    @Schema(description = "Lowest mood level recorded")
    private int lowestMood;

    @Schema(description = "Distribution of mood categories")
    private Map<String, Long> moodDistribution;

    @Schema(description = "Most common mood category")
    private String dominantMood;
}

