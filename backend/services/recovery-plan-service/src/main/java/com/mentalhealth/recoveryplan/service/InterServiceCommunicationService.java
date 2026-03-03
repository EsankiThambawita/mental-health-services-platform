package com.mentalhealth.recoveryplan.service;

import com.mentalhealth.recoveryplan.dto.AppointmentCompletedEvent;
import com.mentalhealth.recoveryplan.dto.PlanCreatedResponse;
import com.mentalhealth.recoveryplan.dto.RecoveryPlanResponse;
import com.mentalhealth.recoveryplan.model.RecoveryPlan;
import com.mentalhealth.recoveryplan.repository.RecoveryPlanRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * Service for handling inter-service communication
 * 
 * Handles interactions with other microservices (Appointment Booking Service)
 */
@Service
public class InterServiceCommunicationService {

    private final RecoveryPlanRepository repository;
    private final RecoveryPlanService recoveryPlanService;
    private final RestTemplate restTemplate;

    // URL of appointment service (configured in application.properties)
    @Value("${appointment.service.url:http://localhost:8081}")
    private String appointmentServiceUrl;

    public InterServiceCommunicationService(RecoveryPlanRepository repository,
            RecoveryPlanService recoveryPlanService,
            RestTemplate restTemplate) {
        this.repository = repository;
        this.recoveryPlanService = recoveryPlanService;
        this.restTemplate = restTemplate;
    }

    /**
     * Handle appointment completion event from Appointment Service
     * 
     * This method is called when Appointment Service notifies us that
     * an appointment has been completed.
     * 
     * Business Logic:
     * 1. Create a recovery plan template for the patient
     * 2. Link it to the appointment
     * 3. Notify appointment service back (optional)
     * 
     * @param event - Appointment completion data
     * @return Response with created plan details
     */
    public PlanCreatedResponse handleAppointmentCompleted(AppointmentCompletedEvent event) {
        try {
            // Create a recovery plan based on appointment
            RecoveryPlan plan = new RecoveryPlan(
                    event.getPatientId(),
                    event.getCounselorId(),
                    "Recovery Plan - " + formatDate(event.getAppointmentDate()),
                    "Recovery plan created from appointment on " + formatDate(event.getAppointmentDate()) +
                            (event.getAppointmentNotes() != null ? ". Notes: " + event.getAppointmentNotes() : ""),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMonths(3) // Default 3-month plan
            );

            // Link to appointment
            plan.setAppointmentId(event.getAppointmentId());

            // Save plan
            RecoveryPlan savedPlan = repository.save(plan);

            // Optional: Notify appointment service that plan was created
            notifyAppointmentService(event.getAppointmentId(), savedPlan.getId());

            // Return success response
            return new PlanCreatedResponse(
                    savedPlan.getId(),
                    event.getAppointmentId(),
                    "Recovery plan created successfully",
                    true);

        } catch (Exception e) {
            // Return error response
            return new PlanCreatedResponse(
                    null,
                    event.getAppointmentId(),
                    "Failed to create recovery plan: " + e.getMessage(),
                    false);
        }
    }

    /**
     * Notify Appointment Service that a recovery plan was created
     * 
     * This is an example of bidirectional communication - we call back
     * to the appointment service to update it with the plan ID
     * 
     * @param appointmentId - The appointment that triggered plan creation
     * @param planId        - The created plan ID
     */
    private void notifyAppointmentService(String appointmentId, String planId) {
        try {
            String url = appointmentServiceUrl + "/api/appointments/" + appointmentId + "/recovery-plan";

            // Create payload
            NotificationPayload payload = new NotificationPayload(planId);

            // Make HTTP POST call to appointment service
            restTemplate.postForObject(url, payload, String.class);

            System.out.println(
                    "✅ Notified appointment service: plan " + planId + " linked to appointment " + appointmentId);

        } catch (Exception e) {
            // Log error but don't fail the entire operation
            System.err.println("⚠️ Failed to notify appointment service: " + e.getMessage());
            // In production, you might want to use a message queue for reliability
        }
    }

    /**
     * Check if a recovery plan exists for an appointment
     * 
     * @param appointmentId - Appointment to check
     * @return true if plan exists, false otherwise
     */
    public boolean hasRecoveryPlan(String appointmentId) {
        return repository.findAll().stream()
                .anyMatch(plan -> appointmentId.equals(plan.getAppointmentId()));
    }

    /**
     * Get recovery plan for an appointment
     * 
     * @param appointmentId - Appointment ID
     * @return Plan response or null
     */
    public RecoveryPlanResponse getRecoveryPlanByAppointment(String appointmentId) {
        return repository.findAll().stream()
                .filter(plan -> appointmentId.equals(plan.getAppointmentId()))
                .findFirst()
                .map(recoveryPlanService::convertToResponse)
                .orElse(null);
    }

    // Helper method to format date
    private String formatDate(LocalDateTime date) {
        if (date == null)
            return "Unknown Date";
        return date.toLocalDate().toString();
    }

    // Inner class for notification payload
    private static class NotificationPayload {
        private String recoveryPlanId;

        public NotificationPayload(String recoveryPlanId) {
            this.recoveryPlanId = recoveryPlanId;
        }

        public String getRecoveryPlanId() {
            return recoveryPlanId;
        }

        public void setRecoveryPlanId(String recoveryPlanId) {
            this.recoveryPlanId = recoveryPlanId;
        }
    }
}