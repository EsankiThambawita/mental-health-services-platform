package com.nsbm.health.counselor.repository;

import com.nsbm.health.counselor.model.CounselorProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CounselorProfileRepository
        extends MongoRepository<CounselorProfile, String> {

    List<CounselorProfile> findBySpecialization(String specialization);

    List<CounselorProfile> findByLanguage(String language);

    List<CounselorProfile> findBySpecializationAndLanguage(
            String specialization,
            String language
    );
}