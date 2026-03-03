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

import com.mentalhealth.recoveryplan.client.UserClient;
import com.mentalhealth.recoveryplan.dto.CreateRecoveryPlanRequest;
import com.mentalhealth.recoveryplan.dto.CreateTaskRequest;
import com.mentalhealth.recoveryplan.dto.RecoveryPlanResponse;
import com.mentalhealth.recoveryplan.dto.UpdatePlanStatusRequest;
import com.mentalhealth.recoveryplan.service.RecoveryPlanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recovery-plans")
@CrossOrigin(origins = "*")
public class RecoveryPlanController {

    private final RecoveryPlanService service;
    private final UserClient userClient;

    public RecoveryPlanController(RecoveryPlanService service, UserClient userClient) {
        this.service = service;
        this.userClient = userClient;
    }

    // ---------------- Counselor Endpoints ----------------

    @PostMapping
    public ResponseEntity<RecoveryPlanResponse> createPlan(
            @RequestHeader("user-id") String userId,
            @Valid @RequestBody CreateRecoveryPlanRequest request) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"Counselor".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse response = service.createPlan(request, user.id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/counselor/plans")
    public ResponseEntity<List<RecoveryPlanResponse>> getPlansByCounselor(
            @RequestHeader("user-id") String userId) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"Counselor".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<RecoveryPlanResponse> plans = service.getPlansByCounselor(user.id);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}/counselor")
    public ResponseEntity<RecoveryPlanResponse> getPlanByCounselor(
            @RequestHeader("user-id") String userId,
            @PathVariable String planId) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"Counselor".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.getPlanByCounselor(planId, user.id);
        return ResponseEntity.ok(plan);
    }

    @PatchMapping("/{planId}/counselor/status")
    public ResponseEntity<RecoveryPlanResponse> updatePlanStatus(
            @RequestHeader("user-id") String userId,
            @PathVariable String planId,
            @Valid @RequestBody UpdatePlanStatusRequest request) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"Counselor".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.updatePlanStatus(planId, user.id, request);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{planId}/counselor")
    public ResponseEntity<Void> deletePlan(
            @RequestHeader("user-id") String userId,
            @PathVariable String planId) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"Counselor".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        service.deletePlan(planId, user.id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- User Endpoints ----------------

    @GetMapping("/patient/plans")
    public ResponseEntity<List<RecoveryPlanResponse>> getPlansByPatient(
            @RequestHeader("user-id") String userId) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"User".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<RecoveryPlanResponse> plans = service.getPlansByPatient(user.id);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}/patient")
    public ResponseEntity<RecoveryPlanResponse> getPlanByPatient(
            @RequestHeader("user-id") String userId,
            @PathVariable String planId) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"User".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.getPlanByPatient(planId, user.id);
        return ResponseEntity.ok(plan);
    }

    @PatchMapping("/{planId}/patient/tasks/{taskId}/complete")
    public ResponseEntity<RecoveryPlanResponse> completeTask(
            @RequestHeader("user-id") String userId,
            @PathVariable String planId,
            @PathVariable String taskId) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"User".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.completeTask(planId, taskId, user.id);
        return ResponseEntity.ok(plan);
    }

    // ---------------- Counselor Task Management ----------------

    @PostMapping("/{planId}/counselor/tasks")
    public ResponseEntity<RecoveryPlanResponse> addTask(
            @RequestHeader("user-id") String userId,
            @PathVariable String planId,
            @Valid @RequestBody CreateTaskRequest request) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"Counselor".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.addTask(planId, user.id, request);
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    @PutMapping("/{planId}/counselor/tasks/{taskId}")
    public ResponseEntity<RecoveryPlanResponse> updateTask(
            @RequestHeader("user-id") String userId,
            @PathVariable String planId,
            @PathVariable String taskId,
            @Valid @RequestBody CreateTaskRequest request) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"Counselor".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.updateTask(planId, taskId, user.id, request);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{planId}/counselor/tasks/{taskId}")
    public ResponseEntity<RecoveryPlanResponse> deleteTask(
            @RequestHeader("user-id") String userId,
            @PathVariable String planId,
            @PathVariable String taskId) {

        UserClient.UserDTO user = userClient.getUserById(userId);

        if (!"Counselor".equals(user.role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        RecoveryPlanResponse plan = service.deleteTask(planId, taskId, user.id);
        return ResponseEntity.ok(plan);
    }
}