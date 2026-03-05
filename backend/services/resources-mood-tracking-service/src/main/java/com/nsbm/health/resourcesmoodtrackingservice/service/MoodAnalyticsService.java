package com.nsbm.health.resourcesmoodtrackingservice.service;

import com.nsbm.health.resourcesmoodtrackingservice.model.MoodEntry;
import com.nsbm.health.resourcesmoodtrackingservice.model.MoodSummary;
import com.nsbm.health.resourcesmoodtrackingservice.repository.MoodEntryRepository;
import com.nsbm.health.resourcesmoodtrackingservice.repository.MoodSummaryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for calculating mood analytics and trends
 */
@Service
@AllArgsConstructor
public class MoodAnalyticsService {

    private final MoodEntryRepository moodEntryRepository;
    private final MoodSummaryRepository moodSummaryRepository;

    // Generate daily summary
    public MoodSummary generateDailySummary(String userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<MoodEntry> dailyEntries = moodEntryRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        MoodSummary summary = new MoodSummary();
        summary.setUserId(userId);
        summary.setPeriod("DAILY");
        summary.setPeriodStart(startOfDay);
        summary.setPeriodEnd(endOfDay);
        summary.setTotalEntries(dailyEntries.size());

        if (!dailyEntries.isEmpty()) {
            double avgMood = dailyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .average()
                    .orElse(0.0);
            summary.setAverageMood(avgMood);

            Integer highestMood = dailyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .max()
                    .orElse(0);
            summary.setHighestMood(highestMood);

            Integer lowestMood = dailyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .min()
                    .orElse(0);
            summary.setLowestMood(lowestMood);

            summary.setMoodCategoryFrequency(calculateCategoryFrequency(dailyEntries));
            summary.setDominantMood(getDominantMoodCategory(dailyEntries));
        }

        summary.prePersist();
        return moodSummaryRepository.save(summary);
    }

    // Generate weekly summary
    public MoodSummary generateWeeklySummary(String userId, LocalDate weekStart) {
        LocalDateTime startOfWeek = weekStart.atStartOfDay();
        LocalDateTime endOfWeek = weekStart.plusDays(6).atTime(23, 59, 59);

        List<MoodEntry> weeklyEntries = moodEntryRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfWeek, endOfWeek);

        MoodSummary summary = new MoodSummary();
        summary.setUserId(userId);
        summary.setPeriod("WEEKLY");
        summary.setPeriodStart(startOfWeek);
        summary.setPeriodEnd(endOfWeek);
        summary.setTotalEntries(weeklyEntries.size());

        if (!weeklyEntries.isEmpty()) {
            double avgMood = weeklyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .average()
                    .orElse(0.0);
            summary.setAverageMood(avgMood);

            Integer highestMood = weeklyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .max()
                    .orElse(0);
            summary.setHighestMood(highestMood);

            Integer lowestMood = weeklyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .min()
                    .orElse(0);
            summary.setLowestMood(lowestMood);

            summary.setMoodCategoryFrequency(calculateCategoryFrequency(weeklyEntries));
            summary.setDominantMood(getDominantMoodCategory(weeklyEntries));
        }

        summary.prePersist();
        return moodSummaryRepository.save(summary);
    }

    // Generate monthly summary
    public MoodSummary generateMonthlySummary(String userId, YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        List<MoodEntry> monthlyEntries = moodEntryRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfMonth, endOfMonth);

        MoodSummary summary = new MoodSummary();
        summary.setUserId(userId);
        summary.setPeriod("MONTHLY");
        summary.setPeriodStart(startOfMonth);
        summary.setPeriodEnd(endOfMonth);
        summary.setTotalEntries(monthlyEntries.size());

        if (!monthlyEntries.isEmpty()) {
            double avgMood = monthlyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .average()
                    .orElse(0.0);
            summary.setAverageMood(avgMood);

            Integer highestMood = monthlyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .max()
                    .orElse(0);
            summary.setHighestMood(highestMood);

            Integer lowestMood = monthlyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .min()
                    .orElse(0);
            summary.setLowestMood(lowestMood);

            summary.setMoodCategoryFrequency(calculateCategoryFrequency(monthlyEntries));
            summary.setDominantMood(getDominantMoodCategory(monthlyEntries));
        }

        summary.prePersist();
        return moodSummaryRepository.save(summary);
    }

    // Get all summaries for a user
    public List<MoodSummary> getUserSummaries(String userId) {
        return moodSummaryRepository.findByUserIdOrderByGeneratedAtDesc(userId);
    }

    // Delete a summary by ID
    public boolean deleteSummary(String id) {
        if (moodSummaryRepository.existsById(id)) {
            moodSummaryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Count frequency of each mood category
    private Map<String, Integer> calculateCategoryFrequency(List<MoodEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(
                        MoodEntry::getMoodCategory,
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                Long::intValue
                        )
                ));
    }

    // Find the most common mood category
    private String getDominantMoodCategory(List<MoodEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(MoodEntry::getMoodCategory, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }
}

