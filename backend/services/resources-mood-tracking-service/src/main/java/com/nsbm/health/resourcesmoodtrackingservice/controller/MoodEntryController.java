package com.nsbm.health.resourcesmoodtrackingservice.controller;

import com.nsbm.health.resourcesmoodtrackingservice.dto.MoodStatistics;
import com.nsbm.health.resourcesmoodtrackingservice.model.MoodEntry;
import com.nsbm.health.resourcesmoodtrackingservice.service.MoodEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * API Controller for mood entry endpoints
 * Handles HTTP requests to create, read, update, and delete mood entries
 */
@RestController
@RequestMapping("/v1/mood-entries")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Mood Entry", description = "APIs for managing mood entries")
public class MoodEntryController {

    private final MoodEntryService moodEntryService;

    /**
     * POST - Create a new mood entry
     */
    @Operation(summary = "Create a new mood entry", description = "Creates a new mood entry for a user")
    @PostMapping
    public ResponseEntity<MoodEntry> createMoodEntry(@Valid @RequestBody MoodEntry moodEntry) {
        // Save the mood entry and return it with 201 Created status
        MoodEntry created = moodEntryService.createMoodEntry(moodEntry);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET - Find a mood entry by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MoodEntry> getMoodEntryById(@PathVariable String id) {
        // Return the entry if found, otherwise return 404
        Optional<MoodEntry> moodEntry = moodEntryService.getMoodEntryById(id);
        return moodEntry.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET - Get all mood entries for a user, sorted by newest first
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MoodEntry>> getUserMoodEntries(@PathVariable String userId) {
        // Return entries sorted by creation date, most recent first
        List<MoodEntry> entries = moodEntryService.getUserMoodEntriesLatest(userId);
        return ResponseEntity.ok(entries);
    }

    /**
     * GET - Get entries within a date range
     */
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<MoodEntry>> getMoodEntriesByDateRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // Return entries between the start and end dates
        List<MoodEntry> entries = moodEntryService.getMoodEntriesByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(entries);
    }

    /**
     * GET - Get entries by mood category
     */
    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<MoodEntry>> getMoodEntriesByCategory(
            @PathVariable String userId,
            @PathVariable String category) {
        // Return entries with this mood category
        List<MoodEntry> entries = moodEntryService.getMoodEntriesByCategory(userId, category);
        return ResponseEntity.ok(entries);
    }

    /**
     * Get low mood entries
     */
    @GetMapping("/user/{userId}/low-mood")
    public ResponseEntity<List<MoodEntry>> getLowMoodEntries(
            @PathVariable String userId,
            @RequestParam(defaultValue = "3") Integer threshold) {
        List<MoodEntry> entries = moodEntryService.getLowMoodEntries(userId, threshold);
        return ResponseEntity.ok(entries);
    }

    /**
     * Update a mood entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<MoodEntry> updateMoodEntry(
            @PathVariable String id,
            @Valid @RequestBody MoodEntry moodEntry) {
        MoodEntry updated = moodEntryService.updateMoodEntry(id, moodEntry);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Archive a mood entry
     */
    @PutMapping("/{id}/archive")
    public ResponseEntity<MoodEntry> archiveMoodEntry(@PathVariable String id) {
        MoodEntry archived = moodEntryService.archiveMoodEntry(id);
        if (archived != null) {
            return ResponseEntity.ok(archived);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a mood entry
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoodEntry(@PathVariable String id) {
        if (moodEntryService.deleteMoodEntry(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get mood statistics for a user
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<MoodStatistics> getMoodStatistics(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        MoodStatistics statistics = moodEntryService.getMoodStatistics(userId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}
