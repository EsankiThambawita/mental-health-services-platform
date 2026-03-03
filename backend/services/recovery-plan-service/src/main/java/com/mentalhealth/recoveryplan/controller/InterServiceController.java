package com.mentalhealth.recoveryplan.controller;

import com.mentalhealth.recoveryplan.dto.AppointmentCompletedEvent;
import com.mentalhealth.recoveryplan.dto.PlanCreatedResponse;
import com.mentalhealth.recoveryplan.dto.RecoveryPlanResponse;
import com.mentalhealth.recoveryplan.service.InterServiceCommunicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for inter-service communication endpoints
 * 
 * These endpoints are called by OTHER microservices, not the frontend
 * 
 * Base URL: /api/recovery-plans/internal
 */
@RestController
@RequestMapping("/api/recovery-plans/internal")
@CrossOrigin(origins = "*")
public class InterServiceController {

    private final InterServiceCommunicationService interServiceService;

    public InterServiceController(InterServiceCommunicationService interServiceService) {
        this.interServiceService = interServiceService;
    }

    /**
     * Endpoint called by Appointment Service when appointment is completed
     * 
     * POST /api/recovery-plans/internal/appointment-completed
     * 
     * @param event - Appointment completion data
     * @return Response with created plan details
     * 
     *         FLOW:
     *         1. Appointment Service completes an appointment
     *         2. Appointment Service calls THIS endpoint
     *         3. We create a recovery plan template
     *         4. We return plan ID to Appointment Service
     */
    @PostMapping("/appointment-completed")
    public ResponseEntity<PlanCreatedResponse> handleAppointmentCompleted(
            @RequestBody AppointmentCompletedEvent event) {

        System.out.println("📨 Received appointment completion event: " + event.getAppointmentId());

        PlanCreatedResponse response = interServiceService.handleAppointmentCompleted(event);

        if (response.isSuccess()) {
            System.out.println("✅ Recovery plan created: " + response.getPlanId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            System.err.println("❌ Failed to create recovery plan: " + response.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if a recovery plan exists for an appointment
     * 
     * GET /api/recovery-plans/internal/appointment/{appointmentId}/exists
     * 
     * Appointment Service can call this to check if a plan already exists
     * before trying to create one
     * 
     * @param appointmentId - Appointment to check
     * @return true/false
     */
    @GetMapping("/appointment/{appointmentId}/exists")
    public ResponseEntity<Boolean> checkPlanExists(@PathVariable String appointmentId) {
        boolean exists = interServiceService.hasRecoveryPlan(appointmentId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get recovery plan for a specific appointment
     * 
     * GET /api/recovery-plans/internal/appointment/{appointmentId}
     * 
     * Allows Appointment Service to fetch plan details
     * 
     * @param appointmentId - Appointment ID
     * @return Plan details or 404
     */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<RecoveryPlanResponse> getPlanByAppointment(
            @PathVariable String appointmentId) {

        RecoveryPlanResponse plan = interServiceService.getRecoveryPlanByAppointment(appointmentId);

        if (plan != null) {
            return ResponseEntity.ok(plan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Health check endpoint for inter-service communication
     * 
     * GET /api/recovery-plans/internal/health
     * 
     * Other services can call this to check if this service is running
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Recovery Plan Service is running");
    }
}