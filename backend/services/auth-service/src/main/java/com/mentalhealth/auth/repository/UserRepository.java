package com.mentalhealth.auth.repository;

import com.mentalhealth.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists (for signup validation)
     */
    boolean existsByEmail(String email);
}