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

/**
 * REST API for mood analytics
 */
@RestController
@RequestMapping("/v1/mood-analytics")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Mood Analytics", description = "APIs for mood analytics and summaries")
public class MoodAnalyticsController {

    private final MoodAnalyticsService moodAnalyticsService;

    // Generate daily mood summary
    @PostMapping("/daily/{userId}")
    public ResponseEntity<MoodSummary> generateDailySummary(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MoodSummary summary = moodAnalyticsService.generateDailySummary(userId, date);
        return new ResponseEntity<>(summary, HttpStatus.CREATED);
    }

    // Generate weekly mood summary
    @PostMapping("/weekly/{userId}")
    public ResponseEntity<MoodSummary> generateWeeklySummary(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        MoodSummary summary = moodAnalyticsService.generateWeeklySummary(userId, weekStart);
        return new ResponseEntity<>(summary, HttpStatus.CREATED);
    }

    // Generate monthly mood summary
    @PostMapping("/monthly/{userId}")
    public ResponseEntity<MoodSummary> generateMonthlySummary(
            @PathVariable String userId,
            @RequestParam String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        MoodSummary summary = moodAnalyticsService.generateMonthlySummary(userId, yearMonth);
        return new ResponseEntity<>(summary, HttpStatus.CREATED);
    }

    // Get all summaries for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MoodSummary>> getUserSummaries(@PathVariable String userId) {
        List<MoodSummary> summaries = moodAnalyticsService.getUserSummaries(userId);
        return ResponseEntity.ok(summaries);
    }


    // Delete a summary
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSummary(@PathVariable String id) {
        if (moodAnalyticsService.deleteSummary(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
