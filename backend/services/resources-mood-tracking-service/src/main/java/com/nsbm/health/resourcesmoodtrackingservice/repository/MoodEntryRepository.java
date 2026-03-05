package com.nsbm.health.resourcesmoodtrackingservice.repository;

import com.nsbm.health.resourcesmoodtrackingservice.model.MoodEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for MoodEntry CRUD operations and custom queries
 */
@Repository
public interface MoodEntryRepository extends MongoRepository<MoodEntry, String> {

    List<MoodEntry> findByUserIdOrderByCreatedAtDesc(String userId);

    List<MoodEntry> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
}
