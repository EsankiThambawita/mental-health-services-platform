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

// REST API for mood entries
@RestController
@RequestMapping("/v1/mood-entries")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Mood Entry", description = "APIs for managing mood entries")
public class MoodEntryController {

    private final MoodEntryService moodEntryService;

    // Create new mood entry
    @Operation(summary = "Create a new mood entry", description = "Creates a new mood entry for a user")
    @PostMapping
    public ResponseEntity<MoodEntry> createMoodEntry(@Valid @RequestBody MoodEntry moodEntry) {
        MoodEntry created = moodEntryService.createMoodEntry(moodEntry);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get mood entry by ID
    @GetMapping("/{id}")
    public ResponseEntity<MoodEntry> getMoodEntryById(@PathVariable String id) {
        Optional<MoodEntry> moodEntry = moodEntryService.getMoodEntryById(id);
        return moodEntry.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all entries for a user (newest first)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MoodEntry>> getUserMoodEntries(@PathVariable String userId) {
        List<MoodEntry> entries = moodEntryService.getUserMoodEntriesLatest(userId);
        return ResponseEntity.ok(entries);
    }

    // Update mood entry
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

    // Archive a mood entry
    @PutMapping("/{id}/archive")
    public ResponseEntity<MoodEntry> archiveMoodEntry(@PathVariable String id) {
        MoodEntry archived = moodEntryService.archiveMoodEntry(id);
        if (archived != null) {
            return ResponseEntity.ok(archived);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete mood entry
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoodEntry(@PathVariable String id) {
        if (moodEntryService.deleteMoodEntry(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Get mood stats for a date range
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<MoodStatistics> getMoodStatistics(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        MoodStatistics statistics = moodEntryService.getMoodStatistics(userId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}
