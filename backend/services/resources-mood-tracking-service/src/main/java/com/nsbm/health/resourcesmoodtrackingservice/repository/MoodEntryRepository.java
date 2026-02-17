package com.nsbm.health.resourcesmoodtrackingservice.repository;

import com.nsbm.health.resourcesmoodtrackingservice.model.MoodEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for MoodEntry CRUD operations and custom queries
 */
@Repository
public interface MoodEntryRepository extends MongoRepository<MoodEntry, String> {

    List<MoodEntry> findByUserId(String userId);

    List<MoodEntry> findByUserIdAndArchivedFalse(String userId);

    List<MoodEntry> findByUserIdOrderByCreatedAtDesc(String userId);

    List<MoodEntry> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    List<MoodEntry> findByUserIdAndMoodCategory(String userId, String moodCategory);

    List<MoodEntry> findByUserIdAndMoodLevelBetween(String userId, Integer minLevel, Integer maxLevel);

    @Query("{'userId': ?0, 'createdAt': {'$gte': ?1, '$lte': ?2}}")
    List<MoodEntry> findMoodEntriesInDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{'userId': ?0, 'moodLevel': {'$lte': ?1}}")
    List<MoodEntry> findLowMoodEntries(String userId, Integer threshold);

    long countByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<MoodEntry> findFirstByUserIdOrderByCreatedAtDesc(String userId);
}

