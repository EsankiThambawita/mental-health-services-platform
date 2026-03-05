package com.nsbm.health.resourcesmoodtrackingservice.repository;

import com.nsbm.health.resourcesmoodtrackingservice.model.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for Resource CRUD operations and custom queries
 */
@Repository
public interface ResourceRepository extends MongoRepository<Resource, String> {

    List<Resource> findByIsActiveTrue();
}
