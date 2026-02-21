package com.nsbm.health.resourcesmoodtrackingservice.repository;

import com.nsbm.health.resourcesmoodtrackingservice.model.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for Resource CRUD operations and custom queries
 */
@Repository
public interface ResourceRepository extends MongoRepository<Resource, String> {

    List<Resource> findByCategory(String category);

    List<Resource> findByIsActiveTrue();

    List<Resource> findByCategoryAndIsActiveTrue(String category);

    List<Resource> findByResourceType(String resourceType);

    @Query("{'recommendedMoodLevels': {$in: [?0]}}")
    List<Resource> findResourcesByMoodLevel(Integer moodLevel);

    @Query("{'recommendedMoodLevels': {$in: [?0]}, 'isActive': true}")
    List<Resource> findActiveResourcesByMoodLevel(Integer moodLevel);

    @Query("{'$text': {'$search': ?0}, 'isActive': true}")
    List<Resource> searchResources(String searchText);

    @Query("{'category': ?0, 'isActive': true}")
    List<Resource> findActiveByCategoryOrderByViewCount(String category);

    List<Resource> findByDifficulty(String difficulty);
}
