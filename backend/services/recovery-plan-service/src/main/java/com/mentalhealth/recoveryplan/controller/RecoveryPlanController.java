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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mentalhealth.recoveryplan.client.AuthClient;
import com.mentalhealth.recoveryplan.dto.CreateRecoveryPlanRequest;
import com.mentalhealth.recoveryplan.dto.CreateTaskRequest;
import com.mentalhealth.recoveryplan.dto.RecoveryPlanResponse;
import com.mentalhealth.recoveryplan.dto.UpdatePlanStatusRequest;
import com.mentalhealth.recoveryplan.service.RecoveryPlanService;

import jakarta.validation.Valid;

/**
 * RecoveryPlanController with Auth Service token validation
 * 
 * INTER-SERVICE COMMUNICATION: Validates tokens with Auth Service on every
 * request
 */

@RestController
@RequestMapping("/api/recovery-plans")
@CrossOrigin(origins = "*")
public class RecoveryPlanController {

    private final RecoveryPlanService service;
    private final AuthClient authClient;
    private static final String COUNSELOR = "COUNSELOR";
    private static final String PATIENT = "PATIENT";

    public RecoveryPlanController(RecoveryPlanService service, AuthClient authClient) {
        this.service = service;
        this.authClient = authClient;
    }

    // ---------------- Counselor Endpoints ----------------

    @PostMapping
    public ResponseEntity<RecoveryPlanResponse> createPlan(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateRecoveryPlanRequest request) {

        // Validate token with Auth Service
        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!COUNSELOR.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Use userId from token as counselorId
        RecoveryPlanResponse response = service.createPlan(request, user.userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/counselor/plans")
    public ResponseEntity<List<RecoveryPlanResponse>> getPlansByCounselor(
            @RequestHeader("Authorization") String token) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!COUNSELOR.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<RecoveryPlanResponse> plans = service.getPlansByCounselor(user.userId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}/counselor")
    public ResponseEntity<RecoveryPlanResponse> getPlanByCounselor(
            @RequestHeader("Authorization") String token,
            @PathVariable String planId) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!COUNSELOR.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.getPlanByCounselor(planId, user.userId);
        return ResponseEntity.ok(plan);
    }

    @PatchMapping("/{planId}/counselor/status")
    public ResponseEntity<RecoveryPlanResponse> updatePlanStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String planId,
            @Valid @RequestBody UpdatePlanStatusRequest request) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!COUNSELOR.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.updatePlanStatus(planId, user.userId, request);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{planId}/counselor")
    public ResponseEntity<Void> deletePlan(
            @RequestHeader("Authorzation") String token,
            @PathVariable String planId) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!COUNSELOR.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        service.deletePlan(planId, user.userId);
        return ResponseEntity.noContent().build();
    }

    // ---------------- User Endpoints ----------------

    @GetMapping("/patient/plans")
    public ResponseEntity<List<RecoveryPlanResponse>> getPlansByPatient(
            @RequestHeader("Authorization") String token) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!PATIENT.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<RecoveryPlanResponse> plans = service.getPlansByPatient(user.userId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}/patient")
    public ResponseEntity<RecoveryPlanResponse> getPlanByPatient(
            @RequestHeader("Authorization") String token,
            @PathVariable String planId) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!PATIENT.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.getPlanByPatient(planId, user.userId);
        return ResponseEntity.ok(plan);
    }

    @PatchMapping("/{planId}/patient/tasks/{taskId}/complete")
    public ResponseEntity<RecoveryPlanResponse> completeTask(
            @RequestHeader("Authorization") String token,
            @PathVariable String planId,
            @PathVariable String taskId) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!PATIENT.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.completeTask(planId, taskId, user.userId);
        return ResponseEntity.ok(plan);
    }

    // ---------------- Counselor Task Management ----------------

    @PostMapping("/{planId}/counselor/tasks")
    public ResponseEntity<RecoveryPlanResponse> addTask(
            @RequestHeader("Authorization") String token,
            @PathVariable String planId,
            @Valid @RequestBody CreateTaskRequest request) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!COUNSELOR.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.addTask(planId, user.userId, request);
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    @PutMapping("/{planId}/counselor/tasks/{taskId}")
    public ResponseEntity<RecoveryPlanResponse> updateTask(
            @RequestHeader("user-id") String token,
            @PathVariable String planId,
            @PathVariable String taskId,
            @Valid @RequestBody CreateTaskRequest request) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!COUNSELOR.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.updateTask(planId, taskId, user.userId, request);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{planId}/counselor/tasks/{taskId}")
    public ResponseEntity<RecoveryPlanResponse> deleteTask(
            @RequestHeader("Authorization") String token,
            @PathVariable String planId,
            @PathVariable String taskId) {

        AuthClient.ValidatedUser user = authClient.validateToken(token);

        if (!COUNSELOR.equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.deleteTask(planId, taskId, user.userId);
        return ResponseEntity.ok(plan);
    }
}