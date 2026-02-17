package com.nsbm.health.resourcesmoodtrackingservice.service;

import com.nsbm.health.resourcesmoodtrackingservice.dto.MoodStatistics;
import com.nsbm.health.resourcesmoodtrackingservice.model.MoodEntry;
import com.nsbm.health.resourcesmoodtrackingservice.repository.MoodEntryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing user mood log entries
 */
@Service
@AllArgsConstructor
public class MoodEntryService {

    private final MoodEntryRepository moodEntryRepository;

    /**
     * Save a new mood entry to the database
     */
    public MoodEntry createMoodEntry(MoodEntry moodEntry) {
        // Set timestamps before saving
        moodEntry.prePersist();
        // Save and return the entry
        return moodEntryRepository.save(moodEntry);
    }

    /**
     * Find a mood entry by its ID
     */
    public Optional<MoodEntry> getMoodEntryById(String id) {
        // Return the entry if found
        return moodEntryRepository.findById(id);
    }

    /**
     * Get all non-archived mood entries for a user
     */
    public List<MoodEntry> getUserMoodEntries(String userId) {
        // Get active (non-deleted) entries for this user
        return moodEntryRepository.findByUserIdAndArchivedFalse(userId);
    }

    /**
     * Get all mood entries for a user, sorted by newest first
     */
    public List<MoodEntry> getUserMoodEntriesLatest(String userId) {
        // Return entries ordered by creation date, newest first
        return moodEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Find mood entries within a specific date range
     */
    public List<MoodEntry> getMoodEntriesByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Get entries between start and end dates
        return moodEntryRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    /**
     * Find mood entries by mood category
     */
    public List<MoodEntry> getMoodEntriesByCategory(String userId, String category) {
        // Get entries that have this mood category (happy, sad, anxious, etc)
        return moodEntryRepository.findByUserIdAndMoodCategory(userId, category);
    }

    /**
     * Find entries where mood level is within a range
     */
    public List<MoodEntry> getMoodEntriesByLevelRange(String userId, Integer minLevel, Integer maxLevel) {
        // Get entries where mood level is between min and max (1-10)
        return moodEntryRepository.findByUserIdAndMoodLevelBetween(userId, minLevel, maxLevel);
    }

    /**
     * Find low mood entries (below a certain threshold)
     */
    public List<MoodEntry> getLowMoodEntries(String userId, Integer threshold) {
        // Get entries where mood level is at or below the threshold
        return moodEntryRepository.findLowMoodEntries(userId, threshold);
    }

    /**
     * Get the most recent mood entry for a user
     */
    public Optional<MoodEntry> getLatestMoodEntry(String userId) {
        // Return the most recent entry
        return moodEntryRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Update an existing mood entry with new information
     */
    public MoodEntry updateMoodEntry(String id, MoodEntry moodEntry) {
        // Find the entry by ID
        Optional<MoodEntry> existingEntry = moodEntryRepository.findById(id);
        if (existingEntry.isPresent()) {
            // Update the fields
            MoodEntry entry = existingEntry.get();
            entry.setMoodLevel(moodEntry.getMoodLevel());
            entry.setMoodCategory(moodEntry.getMoodCategory());
            entry.setNotes(moodEntry.getNotes());
            entry.setUpdatedAt(LocalDateTime.now());
            // Save and return updated entry
            return moodEntryRepository.save(entry);
        }
        return null;
    }

    /**
     * Mark a mood entry as archived (soft delete)
     */
    public MoodEntry archiveMoodEntry(String id) {
        // Find the entry
        Optional<MoodEntry> existingEntry = moodEntryRepository.findById(id);
        if (existingEntry.isPresent()) {
            // Mark as archived instead of deleting
            MoodEntry entry = existingEntry.get();
            entry.setArchived(true);
            entry.setUpdatedAt(LocalDateTime.now());
            // Save and return
            return moodEntryRepository.save(entry);
        }
        return null;
    }

    /**
     * Delete a mood entry from the database
     */
    public boolean deleteMoodEntry(String id) {
        // Check if exists, then delete it
        if (moodEntryRepository.existsById(id)) {
            moodEntryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Count how many mood entries exist in a date range
     */
    public long countMoodEntriesInRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Return the total count of entries in this period
        return moodEntryRepository.countByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    /**
     * Calculate the average mood level over a date range
     */
    public Double calculateAverageMoodLevel(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Get all entries in the range
        List<MoodEntry> entries = getMoodEntriesByDateRange(userId, startDate, endDate);
        if (entries.isEmpty()) {
            return 0.0;
        }
        // Calculate and return the average
        return entries.stream()
                .mapToInt(MoodEntry::getMoodLevel)
                .average()
                .orElse(0.0);
    }

    /**
     * Get a summary of mood statistics for a date range
     */
    public MoodStatistics getMoodStatistics(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Get all entries in the range
        List<MoodEntry> entries = getMoodEntriesByDateRange(userId, startDate, endDate);

        if (entries.isEmpty()) {
            return new MoodStatistics();
        }

        // Calculate statistics
        long totalEntries = entries.size();
        double avgMood = entries.stream().mapToInt(MoodEntry::getMoodLevel).average().orElse(0.0);
        int highestMood = entries.stream().mapToInt(MoodEntry::getMoodLevel).max().orElse(0);
        int lowestMood = entries.stream().mapToInt(MoodEntry::getMoodLevel).min().orElse(0);

        // Calculate mood distribution
        Map<String, Long> moodDistribution = entries.stream()
                .collect(Collectors.groupingBy(MoodEntry::getMoodCategory, Collectors.counting()));

        // Find dominant mood
        String dominantMood = moodDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("none");

        // Return statistics object
        return new MoodStatistics(
                userId,
                totalEntries,
                avgMood,
                highestMood,
                lowestMood,
                moodDistribution,
                dominantMood,
                "STABLE"
        );
    }
}

