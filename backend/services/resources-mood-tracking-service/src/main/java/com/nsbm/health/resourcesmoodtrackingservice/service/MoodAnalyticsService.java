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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for calculating mood analytics and trends
 */
@Service
@AllArgsConstructor
public class MoodAnalyticsService {

    private final MoodEntryRepository moodEntryRepository;
    private final MoodSummaryRepository moodSummaryRepository;

    /**
     * Create a daily summary of mood data
     */
    public MoodSummary generateDailySummary(String userId, LocalDate date) {
        // Get all mood entries for this day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<MoodEntry> dailyEntries = moodEntryRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        // Create a new summary object
        MoodSummary summary = new MoodSummary();
        summary.setUserId(userId);
        summary.setPeriod("DAILY");
        summary.setPeriodStart(startOfDay);
        summary.setPeriodEnd(endOfDay);
        summary.setTotalEntries(dailyEntries.size());

        // Calculate stats if we have entries
        if (!dailyEntries.isEmpty()) {
            // Calculate average mood
            double avgMood = dailyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .average()
                    .orElse(0.0);
            summary.setAverageMood(avgMood);

            // Find highest mood level
            Integer highestMood = dailyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .max()
                    .orElse(0);
            summary.setHighestMood(highestMood);

            // Find lowest mood level
            Integer lowestMood = dailyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .min()
                    .orElse(0);
            summary.setLowestMood(lowestMood);

            // Get mood categories and dominant mood
            summary.setMoodCategoryFrequency(calculateCategoryFrequency(dailyEntries));
            summary.setDominantMood(getDominantMoodCategory(dailyEntries));
        }

        // Save to database and return
        summary.prePersist();
        return moodSummaryRepository.save(summary);
    }

    /**
     * Create a weekly summary of mood data
     */
    public MoodSummary generateWeeklySummary(String userId, LocalDate weekStart) {
        // Get all mood entries for this week (7 days)
        LocalDateTime startOfWeek = weekStart.atStartOfDay();
        LocalDateTime endOfWeek = weekStart.plusDays(6).atTime(23, 59, 59);

        List<MoodEntry> weeklyEntries = moodEntryRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfWeek, endOfWeek);

        // Create a new summary object for the week
        MoodSummary summary = new MoodSummary();
        summary.setUserId(userId);
        summary.setPeriod("WEEKLY");
        summary.setPeriodStart(startOfWeek);
        summary.setPeriodEnd(endOfWeek);
        summary.setTotalEntries(weeklyEntries.size());

        // Calculate weekly stats if we have entries
        if (!weeklyEntries.isEmpty()) {
            // Calculate average mood for the week
            double avgMood = weeklyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .average()
                    .orElse(0.0);
            summary.setAverageMood(avgMood);

            // Find highest mood level this week
            Integer highestMood = weeklyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .max()
                    .orElse(0);
            summary.setHighestMood(highestMood);

            // Find lowest mood level this week
            Integer lowestMood = weeklyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .min()
                    .orElse(0);
            summary.setLowestMood(lowestMood);

            // Get mood categories for this week
            summary.setMoodCategoryFrequency(calculateCategoryFrequency(weeklyEntries));
            summary.setDominantMood(getDominantMoodCategory(weeklyEntries));

            // Compare with previous week to see trend
            double previousWeekAvg = getPreviousPeriodAverageMood(userId, "WEEKLY", weekStart);
            if (previousWeekAvg > 0) {
                // Calculate percentage change from last week
                Double trendPercentage = ((avgMood - previousWeekAvg) / previousWeekAvg) * 100;
                summary.setTrendPercentage(trendPercentage);
            }
        }

        // Save to database and return
        summary.prePersist();
        return moodSummaryRepository.save(summary);
    }

    /**
     * Create a monthly summary of mood data
     */
    public MoodSummary generateMonthlySummary(String userId, YearMonth month) {
        // Get all mood entries for this month
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        List<MoodEntry> monthlyEntries = moodEntryRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfMonth, endOfMonth);

        // Create a new summary object for the month
        MoodSummary summary = new MoodSummary();
        summary.setUserId(userId);
        summary.setPeriod("MONTHLY");
        summary.setPeriodStart(startOfMonth);
        summary.setPeriodEnd(endOfMonth);
        summary.setTotalEntries(monthlyEntries.size());

        // Calculate monthly stats if we have entries
        if (!monthlyEntries.isEmpty()) {
            // Calculate average mood for the month
            double avgMood = monthlyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .average()
                    .orElse(0.0);
            summary.setAverageMood(avgMood);

            // Find highest mood level this month
            Integer highestMood = monthlyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .max()
                    .orElse(0);
            summary.setHighestMood(highestMood);

            // Find lowest mood level this month
            Integer lowestMood = monthlyEntries.stream()
                    .mapToInt(MoodEntry::getMoodLevel)
                    .min()
                    .orElse(0);
            summary.setLowestMood(lowestMood);

            // Get mood categories for this month
            summary.setMoodCategoryFrequency(calculateCategoryFrequency(monthlyEntries));
            summary.setDominantMood(getDominantMoodCategory(monthlyEntries));

            // Compare with previous month to see trend
            double previousMonthAvg = getPreviousPeriodAverageMood(userId, "MONTHLY", month.atDay(1));
            if (previousMonthAvg > 0) {
                // Calculate percentage change from last month
                Double trendPercentage = ((avgMood - previousMonthAvg) / previousMonthAvg) * 100;
                summary.setTrendPercentage(trendPercentage);
            }
        }

        // Save to database and return
        summary.prePersist();
        return moodSummaryRepository.save(summary);
    }

    /**
     * Get mood summary by ID
     */
    public Optional<MoodSummary> getMoodSummary(String id) {
        return moodSummaryRepository.findById(id);
    }

    /**
     * Get all summaries for a user
     */
    public List<MoodSummary> getUserSummaries(String userId) {
        return moodSummaryRepository.findByUserIdOrderByPeriodEndDesc(userId);
    }

    /**
     * Get summaries by period
     */
    public List<MoodSummary> getSummariesByPeriod(String userId, String period) {
        return moodSummaryRepository.findByUserIdAndPeriodOrderByPeriodEndDesc(userId, period);
    }

    /**
     * Count how many times each mood category appears
     */
    private Map<String, Integer> calculateCategoryFrequency(List<MoodEntry> entries) {
        // Group entries by their mood category and count them
        return entries.stream()
                .collect(Collectors.groupingBy(
                        MoodEntry::getMoodCategory,
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                Long::intValue
                        )
                ));
    }

    /**
     * Find the mood category that appears most often
     */
    private String getDominantMoodCategory(List<MoodEntry> entries) {
        // Count mood categories and find the one with the highest count
        return entries.stream()
                .collect(Collectors.groupingBy(MoodEntry::getMoodCategory, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

    /**
     * Get the average mood from the previous period (day/week/month)
     */
    private double getPreviousPeriodAverageMood(String userId, String period, LocalDate currentDate) {
        // Calculate the previous period date based on type
        LocalDate previousDate;
        if ("DAILY".equals(period)) {
            previousDate = currentDate.minusDays(1);
        } else if ("WEEKLY".equals(period)) {
            previousDate = currentDate.minusWeeks(1);
        } else if ("MONTHLY".equals(period)) {
            previousDate = currentDate.minusMonths(1);
        } else {
            return 0.0;
        }

        // Get all entries from the previous period
        LocalDateTime startOfPreviousPeriod = previousDate.atStartOfDay();
        LocalDateTime endOfPreviousPeriod = previousDate.atTime(23, 59, 59);

        List<MoodEntry> previousEntries = moodEntryRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfPreviousPeriod, endOfPreviousPeriod);

        // Return 0 if no entries, otherwise return the average
        if (previousEntries.isEmpty()) {
            return 0.0;
        }

        return previousEntries.stream()
                .mapToInt(MoodEntry::getMoodLevel)
                .average()
                .orElse(0.0);
    }


    /**
     * Check if user has a risk pattern (too many low mood entries)
     */
    public boolean hasRiskPattern(String userId) {
        // Get all mood entries from the last 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<MoodEntry> recentEntries = moodEntryRepository
                .findByUserIdAndCreatedAtBetween(userId, sevenDaysAgo, LocalDateTime.now());

        // No risk if no entries exist
        if (recentEntries.isEmpty()) {
            return false;
        }

        // Count how many entries have very low mood (mood level 3 or less)
        long lowMoodCount = recentEntries.stream()
                .filter(e -> e.getMoodLevel() <= 3)
                .count();

        // Risk if more than 50% of recent entries are low mood
        return (double) lowMoodCount / recentEntries.size() > 0.5;
    }
}

