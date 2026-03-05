package com.nsbm.health.resourcesmoodtrackingservice.service;

import com.nsbm.health.resourcesmoodtrackingservice.dto.MoodStatistics;
import com.nsbm.health.resourcesmoodtrackingservice.model.MoodEntry;
import com.nsbm.health.resourcesmoodtrackingservice.repository.MoodEntryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles mood entry business logic
 */
@Service
@AllArgsConstructor
public class MoodEntryService {

    private final MoodEntryRepository moodEntryRepository;

    // Save new mood entry
    public MoodEntry createMoodEntry(MoodEntry moodEntry) {
        moodEntry.prePersist();
        return moodEntryRepository.save(moodEntry);
    }

    // Find mood entry by ID
    public Optional<MoodEntry> getMoodEntryById(String id) {
        return moodEntryRepository.findById(id);
    }

    // Get user's mood entries (newest first)
    public List<MoodEntry> getUserMoodEntriesLatest(String userId) {
        return moodEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Get entries between two dates
    public List<MoodEntry> getMoodEntriesByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return moodEntryRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    // Update mood entry
    public MoodEntry updateMoodEntry(String id, MoodEntry moodEntry) {
        Optional<MoodEntry> existingEntry = moodEntryRepository.findById(id);
        if (existingEntry.isPresent()) {
            MoodEntry entry = existingEntry.get();
            entry.setMoodLevel(moodEntry.getMoodLevel());
            entry.setMoodCategory(moodEntry.getMoodCategory());
            entry.setNotes(moodEntry.getNotes());
            entry.setUpdatedAt(LocalDateTime.now());
            return moodEntryRepository.save(entry);
        }
        return null;
    }

    // Archive mood entry (soft delete)
    public MoodEntry archiveMoodEntry(String id) {
        Optional<MoodEntry> existingEntry = moodEntryRepository.findById(id);
        if (existingEntry.isPresent()) {
            MoodEntry entry = existingEntry.get();
            entry.setArchived(true);
            entry.setUpdatedAt(LocalDateTime.now());
            return moodEntryRepository.save(entry);
        }
        return null;
    }

    // Delete mood entry permanently
    public boolean deleteMoodEntry(String id) {
        if (moodEntryRepository.existsById(id)) {
            moodEntryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Calculate mood stats for a date range
    public MoodStatistics getMoodStatistics(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<MoodEntry> entries = getMoodEntriesByDateRange(userId, startDate, endDate);

        if (entries.isEmpty()) {
            return new MoodStatistics();
        }

        long totalEntries = entries.size();
        double avgMood = entries.stream().mapToInt(MoodEntry::getMoodLevel).average().orElse(0.0);
        int highestMood = entries.stream().mapToInt(MoodEntry::getMoodLevel).max().orElse(0);
        int lowestMood = entries.stream().mapToInt(MoodEntry::getMoodLevel).min().orElse(0);

        Map<String, Long> moodDistribution = entries.stream()
                .collect(Collectors.groupingBy(MoodEntry::getMoodCategory, Collectors.counting()));

        String dominantMood = moodDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("none");

        return new MoodStatistics(
                userId,
                totalEntries,
                avgMood,
                highestMood,
                lowestMood,
                moodDistribution,
                dominantMood
        );
    }
}
