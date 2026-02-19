package com.nsbm.health.resourcesmoodtrackingservice.repository;

import com.nsbm.health.resourcesmoodtrackingservice.model.MoodSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for MoodSummary CRUD operations
 */
@Repository
public interface MoodSummaryRepository extends MongoRepository<MoodSummary, String> {

    List<MoodSummary> findByUserId(String userId);

    List<MoodSummary> findByUserIdAndPeriod(String userId, String period);

    Optional<MoodSummary> findByUserIdAndPeriodAndPeriodEnd(String userId, String period, LocalDateTime periodEnd);

    List<MoodSummary> findByUserIdOrderByPeriodEndDesc(String userId);

    List<MoodSummary> findByUserIdAndPeriodOrderByPeriodEndDesc(String userId, String period);
}

