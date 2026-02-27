package com.nsbm.health.counselor.repository;

import com.nsbm.health.counselor.model.CounselorProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * Handles DB operations automatically
 */
public interface CounselorRepository extends MongoRepository<CounselorProfile, String> {

    List<CounselorProfile> findByLanguagesContaining(String language);
}

