package com.mentalhealth.recoveryplan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mentalhealth.recoveryplan.dto.CreateRecoveryPlanRequest;
import com.mentalhealth.recoveryplan.dto.CreateTaskRequest;
import com.mentalhealth.recoveryplan.dto.RecoveryPlanResponse;
import com.mentalhealth.recoveryplan.dto.TaskResponse;
import com.mentalhealth.recoveryplan.dto.UpdatePlanStatusRequest;
import com.mentalhealth.recoveryplan.exceptions.InvalidOperationException;
import com.mentalhealth.recoveryplan.exceptions.ResourceNotFoundException;
import com.mentalhealth.recoveryplan.exceptions.UnauthorizedAccessException;
import com.mentalhealth.recoveryplan.model.PlanStatus;
import com.mentalhealth.recoveryplan.model.RecoveryPlan;
import com.mentalhealth.recoveryplan.model.RecoveryTask;
import com.mentalhealth.recoveryplan.repository.RecoveryPlanRepository;

// RecoveryPlanService - Business Logic

@Service
public class RecoveryPlanService {

    private final RecoveryPlanRepository repository;

    public RecoveryPlanService(RecoveryPlanRepository repository) {
        this.repository = repository;
    }

    // Counselor Operations
    // Onlyr counselors can create plans. @param request - plan details from api
    public RecoveryPlanResponse createPlan(CreateRecoveryPlanRequest request) {
        boolean hasActivePlan = repository.existsByPatientIdAndStatus(
            request.getPatientId(),
            PlanStatus.ACTIVE
        );

        if (hasActivePlan) {
            throw new InvalidOperationException(
                "Patient already has an active recovery plan."
            );
        }

        // Entity from DTO
        RecoveryPlan plan = new RecoveryPlan(
            request.getPatientId(),
            request.getCounselorId(),
            request.getTitle(),
            request.getDescription(),
            request.getStartDate(),
            request.getEndDate()
        );

        plan.setAppointmentId(request.getAppointmentId());

        RecoveryPlan savedPlan = repository.save(plan);

        return convertToResponse(savedPlan);
    }

    // Get all plans created by a counselor
    public List<RecoveryPlanResponse> getPlansByCounselor(String counselorId) {
        List<RecoveryPlan> plans = repository.findByCounselorId(counselorId);
        return plans.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    // Get a specific plan, only the counselor can access
    public RecoveryPlanResponse getPlanByCounselor(String planId, String counselorId) {
        RecoveryPlan plan = repository.findByIdAndCounselorId(planId, counselorId)
            .orElseThrow(() -> new UnauthorizedAccessException(
                "You do not have permission to access this plan or it does not exist"
            ));
        
        return convertToResponse(plan);
    }

    // Update plan status
    public RecoveryPlanResponse updatePlanStatus(String planId, String counselorId, 
                                                 UpdatePlanStatusRequest request) {
        RecoveryPlan plan = repository.findByIdAndCounselorId(planId, counselorId)
                .orElseThrow(() -> new UnauthorizedAccessException(
                        "You do not have permission to update this plan or it does not exist"
                ));

        if (plan.getStatus() != PlanStatus.ACTIVE && request.getStatus() == PlanStatus.ACTIVE) {
            throw new InvalidOperationException(
                    "Cannot reactivate a completed or cancelled plan. Create a new plan instead."
            );
        }

        plan.setStatus(request.getStatus());
        plan.setUpdatedAt(LocalDateTime.now());
        
        RecoveryPlan updatedPlan = repository.save(plan);
        return convertToResponse(updatedPlan);
    }

    // Delete a recovery plan
    public void deletePlan(String planId, String counselorId) {
        RecoveryPlan plan = repository.findByIdAndCounselorId(planId, counselorId)
                .orElseThrow(() -> new UnauthorizedAccessException(
                        "You do not have permission to delete this plan or it does not exist"
                ));

        repository.delete(plan);
    }


    
    // Patient Operations
    // Get all plans for a patient
    public List<RecoveryPlanResponse> getPlansByPatient(String patientId) {
        List<RecoveryPlan> plans = repository.findByPatientId(patientId);
        return plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get a specific plan
    public RecoveryPlanResponse getPlanByPatient(String planId, String patientId) {
        RecoveryPlan plan = repository.findByIdAndPatientId(planId, patientId)
                .orElseThrow(() -> new UnauthorizedAccessException(
                        "You do not have permission to access this plan or it does not exist"
                ));
        
        return convertToResponse(plan);
    }

    // Patient marks a task as completed
    public RecoveryPlanResponse completeTask(String planId, String taskId, String patientId) {
        RecoveryPlan plan = repository.findByIdAndPatientId(planId, patientId)
                .orElseThrow(() -> new UnauthorizedAccessException(
                        "You dot not have permission to access this plan or it does not exist"
                ));

        if (plan.getStatus() == PlanStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot complete tasks on a cancelled plan");
        }

        RecoveryTask task = plan.getTasks().stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Task", "taskId", taskId));

        if (task.isCompleted()) {
            throw new InvalidOperationException("Task is already completed");
        }

        task.setCompleted(true);
        task.setCompletedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());

        RecoveryPlan updatedPlan = repository.save(plan);
        return convertToResponse(updatedPlan);
    }


    // Task Management - Counselor
    // As a new task
    public RecoveryPlanResponse addTask(String planId, String counselorId, CreateTaskRequest request) {
        RecoveryPlan plan = repository.findByIdAndCounselorId(planId, counselorId)
                .orElseThrow(() -> new UnauthorizedAccessException(
                        "You do not have permission to modify this plan or it does not exist"
                ));

        if (plan.getStatus() == PlanStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot add tasks to a cancelled plan");
        }

        // Generate unique task ID
        String taskId = UUID.randomUUID().toString();

        RecoveryTask task = new RecoveryTask(
                taskId,
                request.getDescription(),
                request.getDueDate(),
                request.getCounselorNotes()
        );

        plan.getTasks().add(task);
        plan.setUpdatedAt(LocalDateTime.now());

        RecoveryPlan updatedPlan = repository.save(plan);
        return convertToResponse(updatedPlan);
    }

    // Update an existing task
    public RecoveryPlanResponse updateTask(String planId, String taskId, String counselorId, 
                                          CreateTaskRequest request) {
        RecoveryPlan plan = repository.findByIdAndCounselorId(planId, counselorId)
                .orElseThrow(() -> new UnauthorizedAccessException(
                        "You don't have permission to modify this plan or it doesn't exist"
                ));

        RecoveryTask task = plan.getTasks().stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Task", "taskId", taskId));

        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setCounselorNotes(request.getCounselorNotes());
        plan.setUpdatedAt(LocalDateTime.now());

        RecoveryPlan updatedPlan = repository.save(plan);
        return convertToResponse(updatedPlan);
    }

    // Delete a task
    public RecoveryPlanResponse deleteTask(String planId, String taskId, String counselorId) {
        RecoveryPlan plan = repository.findByIdAndCounselorId(planId, counselorId)
                .orElseThrow(() -> new UnauthorizedAccessException(
                        "You don't have permission to modify this plan or it doesn't exist"
                ));

        boolean removed = plan.getTasks().removeIf(task -> task.getTaskId().equals(taskId));
        
        if (!removed) {
            throw new ResourceNotFoundException("Task", "taskId", taskId);
        }

        plan.setUpdatedAt(LocalDateTime.now());

        RecoveryPlan updatedPlan = repository.save(plan);
        return convertToResponse(updatedPlan);
    }


    // Helper Methods
    // convertToResponse
    private RecoveryPlanResponse convertToResponse(RecoveryPlan plan) {
        List<TaskResponse> taskResponses = plan.getTasks().stream()
            .map(this::convertTaskToResponse)
            .collect(Collectors.toList());

            return new RecoveryPlanResponse(
                plan.getId(),
                plan.getPatientId(),
                plan.getCounselorId(),
                plan.getAppointmentId(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getStatus(),
                taskResponses,
                plan.getCreatedAt(),
                plan.getUpdatedAt()
            );
    }

    // converTaskToResponse
    private TaskResponse convertTaskToResponse(RecoveryTask task) {
        return new TaskResponse(
                task.getTaskId(),
                task.getDescription(),
                task.getDueDate(),
                task.isCompleted(),
                task.getCompletedAt(),
                task.getCounselorNotes(),
                task.getCreatedAt()
        );
    }
}
