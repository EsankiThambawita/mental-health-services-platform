package com.nsbm.health.resourcesmoodtrackingservice.repository;

import com.nsbm.health.resourcesmoodtrackingservice.model.MoodSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for MoodSummary CRUD operations
 */
@Repository
public interface MoodSummaryRepository extends MongoRepository<MoodSummary, String> {

    List<MoodSummary> findByUserIdOrderByGeneratedAtDesc(String userId);
}

