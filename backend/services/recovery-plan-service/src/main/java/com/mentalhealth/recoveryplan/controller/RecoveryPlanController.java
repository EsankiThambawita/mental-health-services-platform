package com.mentalhealth.recoveryplan.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mentalhealth.recoveryplan.dto.CreateRecoveryPlanRequest;
import com.mentalhealth.recoveryplan.dto.CreateTaskRequest;
import com.mentalhealth.recoveryplan.dto.RecoveryPlanResponse;
import com.mentalhealth.recoveryplan.dto.UpdatePlanStatusRequest;
import com.mentalhealth.recoveryplan.service.RecoveryPlanService;

import jakarta.validation.Valid;

// RecoveryPlanController - REST API endpoints
// http://localhost:8082/api/recovery-plans

@RestController
@RequestMapping("/api/recovery-plans")
@CrossOrigin(origins = "*")
public class RecoveryPlanController {

    private final RecoveryPlanService service;

    public RecoveryPlanController(RecoveryPlanService service) {
        this.service = service;
    }

    // Counselor Endpoints
    // Create Recovery Plan - POST /api/recovery-plans
    @PostMapping
    public ResponseEntity<RecoveryPlanResponse> createPlan(@Valid @RequestBody CreateRecoveryPlanRequest request) {
        RecoveryPlanResponse response = service.createPlan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    // Get all plans created by a counselor - GET
    // /api/recovery-plans/counselor/{counselorId}
    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<List<RecoveryPlanResponse>> getPlansByCounselor(
            @PathVariable String counselorId) {
        List<RecoveryPlanResponse> plans = service.getPlansByCounselor(counselorId);
        return ResponseEntity.ok(plans); // 200
    }

    // Get a specific plan (counselor access) - GET
    // /api/recovery-plans/{planId}/counselor/{counselorId}
    @GetMapping("/{planId}/counselor/{counselorId}")
    public ResponseEntity<RecoveryPlanResponse> getPlanByCounselor(
            @PathVariable String planId,
            @PathVariable String counselorId) {
        RecoveryPlanResponse plan = service.getPlanByCounselor(planId, counselorId);
        return ResponseEntity.ok(plan);
    }

    // Update plan status - PATCH
    // /api/recovery-plans/{planId}/counselor/{counsrlorId}/status
    // PATCH = partial update (just status)
    @PatchMapping("/{planId}/counselor/{counselorId}/status")
    public ResponseEntity<RecoveryPlanResponse> updatePlanStatus(
            @PathVariable String planId,
            @PathVariable String counselorId,
            @Valid @RequestBody UpdatePlanStatusRequest request) {
        RecoveryPlanResponse plan = service.updatePlanStatus(planId, counselorId, request);
        return ResponseEntity.ok(plan);
    }

    // Delete a recovery Plan - DELETE
    // /api/recovery-plans/{planId}/counselor/{counselorId}
    @DeleteMapping("/{planId}/counselor/{counselorId}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable String planId,
            @PathVariable String counselorId) {
        service.deletePlan(planId, counselorId);
        return ResponseEntity.noContent().build(); // 204
    }

    // Patient Endpoints
    // Get all plans for a patient - GET /api/recovery-plans/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RecoveryPlanResponse>> getPlansByPatient(
            @PathVariable String patientId) {
        List<RecoveryPlanResponse> plans = service.getPlansByPatient(patientId);
        return ResponseEntity.ok(plans);
    }

    // Get a specific plan (patient access) - GET
    // /api/recovery-plans/{planId}/patient/{patientId}
    @GetMapping("/{planId}/patient/{patientId}")
    public ResponseEntity<RecoveryPlanResponse> getPlanByPatient(
            @PathVariable String planId,
            @PathVariable String patientId) {
        RecoveryPlanResponse plan = service.getPlanByPatient(planId, patientId);
        return ResponseEntity.ok(plan);
    }

    // Mark task as complete - PATCH
    // /api/recovery-plans/{planId}/patient/{patientId}/tasks/{taskId}/complete
    @PatchMapping("/{planId}/patient/{patientId}/tasks/{taskId}/complete")
    public ResponseEntity<RecoveryPlanResponse> completeTask(
            @PathVariable String planId,
            @PathVariable String patientId,
            @PathVariable String taskId) {
        RecoveryPlanResponse plan = service.completeTask(planId, taskId, patientId);
        return ResponseEntity.ok(plan);
    }

    // Task Management Endpoints (Counselor))
    // Ass a new task to a plan - POST
    // /api/recovery-plans/{planId}/counselor/{counselorId}/tasks
    @PostMapping("/{planId}/counselor/{counselorId}/tasks")
    public ResponseEntity<RecoveryPlanResponse> addTask(
            @PathVariable String planId,
            @PathVariable String counselorId,
            @Valid @RequestBody CreateTaskRequest request) {
        RecoveryPlanResponse plan = service.addTask(planId, counselorId, request);
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    // Update an existing task - PUT
    // /api/recovery-plans/{planId}/counselor/{counselorId}/tasks/{taskId}
    @PutMapping("/{planId}/counselor/{counselorId}/tasks/{taskId}")
    public ResponseEntity<RecoveryPlanResponse> updateTask(
            @PathVariable String planId,
            @PathVariable String counselorId,
            @PathVariable String taskId,
            @Valid @RequestBody CreateTaskRequest request) {
        RecoveryPlanResponse plan = service.updateTask(planId, taskId, counselorId, request);
        return ResponseEntity.ok(plan);
    }

    // Delete a task from a plan - DELETE
    // /api/recovery-plans/{planId}/counselor/{counselorId}/tasks/{taskId}
    @DeleteMapping("/{planId}/counselor/{counselorId}/tasks/{taskId}")
    public ResponseEntity<RecoveryPlanResponse> deleteTask(
            @PathVariable String planId,
            @PathVariable String counselorId,
            @PathVariable String taskId) {
        RecoveryPlanResponse plan = service.deleteTask(planId, taskId, counselorId);
        return ResponseEntity.ok(plan);
    }

}
