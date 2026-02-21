package com.nsbm.health.resourcesmoodtrackingservice.controller;

import com.nsbm.health.resourcesmoodtrackingservice.model.MoodSummary;
import com.nsbm.health.resourcesmoodtrackingservice.service.MoodAnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Mood Analytics operations
 */
@RestController
@RequestMapping("/v1/mood-analytics")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Mood Analytics", description = "APIs for mood analytics and summaries")
public class MoodAnalyticsController {

    private final MoodAnalyticsService moodAnalyticsService;

    /**
     * Generate daily mood summary
     */
    @PostMapping("/daily/{userId}")
    public ResponseEntity<MoodSummary> generateDailySummary(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MoodSummary summary = moodAnalyticsService.generateDailySummary(userId, date);
        return new ResponseEntity<>(summary, HttpStatus.CREATED);
    }

    /**
     * Generate weekly mood summary
     */
    @PostMapping("/weekly/{userId}")
    public ResponseEntity<MoodSummary> generateWeeklySummary(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        MoodSummary summary = moodAnalyticsService.generateWeeklySummary(userId, weekStart);
        return new ResponseEntity<>(summary, HttpStatus.CREATED);
    }

    /**
     * Generate monthly mood summary
     */
    @PostMapping("/monthly/{userId}")
    public ResponseEntity<MoodSummary> generateMonthlySummary(
            @PathVariable String userId,
            @RequestParam String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        MoodSummary summary = moodAnalyticsService.generateMonthlySummary(userId, yearMonth);
        return new ResponseEntity<>(summary, HttpStatus.CREATED);
    }

    /**
     * Get mood summary by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MoodSummary> getMoodSummary(@PathVariable String id) {
        Optional<MoodSummary> summary = moodAnalyticsService.getMoodSummary(id);
        return summary.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all summaries for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MoodSummary>> getUserSummaries(@PathVariable String userId) {
        List<MoodSummary> summaries = moodAnalyticsService.getUserSummaries(userId);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get summaries by period
     */
    @GetMapping("/user/{userId}/period/{period}")
    public ResponseEntity<List<MoodSummary>> getSummariesByPeriod(
            @PathVariable String userId,
            @PathVariable String period) {
        List<MoodSummary> summaries = moodAnalyticsService.getSummariesByPeriod(userId, period);
        return ResponseEntity.ok(summaries);
    }


    /**
     * Check if user has risk pattern
     */
    @GetMapping("/user/{userId}/risk-pattern")
    public ResponseEntity<Boolean> hasRiskPattern(@PathVariable String userId) {
        boolean hasRisk = moodAnalyticsService.hasRiskPattern(userId);
        return ResponseEntity.ok(hasRisk);
    }
}

