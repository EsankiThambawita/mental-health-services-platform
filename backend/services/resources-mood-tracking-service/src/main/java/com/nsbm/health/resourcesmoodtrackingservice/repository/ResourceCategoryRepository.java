package com.nsbm.health.resourcesmoodtrackingservice.repository;

import com.nsbm.health.resourcesmoodtrackingservice.model.ResourceCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for ResourceCategory CRUD operations
 */
@Repository
public interface ResourceCategoryRepository extends MongoRepository<ResourceCategory, String> {

    List<ResourceCategory> findByIsActiveTrue();

    List<ResourceCategory> findByIsActiveTrueOrderByDisplayOrder();

    List<ResourceCategory> findByTags(String tag);
}
