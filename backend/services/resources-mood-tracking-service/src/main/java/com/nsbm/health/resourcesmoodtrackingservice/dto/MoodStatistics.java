package com.nsbm.health.resourcesmoodtrackingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Statistics about a user's mood entries
 * Shows averages, counts, and mood distribution
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Statistics about a user's mood entries")
public class MoodStatistics {

    // The user these statistics belong to
    @Schema(description = "User ID")
    private String userId;

    // How many mood entries in this period
    @Schema(description = "Total number of mood entries")
    private long totalEntries;

    // Average mood level (1-10)
    @Schema(description = "Average mood level (1-10)")
    private double averageMood;

    // The highest mood level recorded
    @Schema(description = "Highest mood level recorded")
    private int highestMood;

    // The lowest mood level recorded
    @Schema(description = "Lowest mood level recorded")
    private int lowestMood;

    // How many times each mood category appears
    // Example: {"happy": 5, "sad": 2, "anxious": 3}
    @Schema(description = "Distribution of mood categories")
    private Map<String, Long> moodDistribution;

    // The most common mood category
    @Schema(description = "Most common mood category")
    private String dominantMood;

    // Is the mood trend going up or down?
    @Schema(description = "Mood trend: IMPROVING, DECLINING, or STABLE")
    private String moodTrend;
}

