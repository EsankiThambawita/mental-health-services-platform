package com.nsbm.health.counselor.repository;

import com.nsbm.health.counselor.model.CounselorProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

/**
 * Handles DB operations automatically
 */
public interface CounselorRepository extends MongoRepository<CounselorProfile, String> {

    @Query("{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'specializations': { '$regex': ?0, '$options': 'i' } }, { 'languages': { '$regex': ?0, '$options': 'i' } } ] }")
    List<CounselorProfile> searchByNameOrSpecialization(String query);
}

