package com.mentalhealth.recoveryplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mentalhealth.recoveryplan.model.PlanStatus;
import com.mentalhealth.recoveryplan.model.RecoveryPlan;

// RecoveryPlanRepository - Database access layer

@Repository
public interface RecoveryPlanRepository extends MongoRepository<RecoveryPlan, String>{

    //Find all plans assigned to a specific patient.
    List<RecoveryPlan> findByPatientId(String patientId);

    // Find all plans by a specific counselor
    List<RecoveryPlan> findByCounselorId(String counselorId);;

    // Find a specific plan by ID, but only if it belongs to the given patient
    Optional<RecoveryPlan> findByIdAndPatientId(String id, String patientId);

    // Find a specific plan by ID, but only if it belongs to the given counselor.
    Optional<RecoveryPlan> findByIdAndCounselorId(String id, String counselorId);

    // Find all plans for a patient filtered by status.
    List<RecoveryPlan> findByPatientIdAndStatus(String patientId, PlanStatus status);

    // Check if a patient already has an active plan
    boolean existsByPatientIdAndStatus(String patientId, PlanStatus status);
}
