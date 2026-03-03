package com.nsbm.health.appointment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Service
public class RecoveryPlanIntegrationService {

    private final RestTemplate restTemplate;

    @Value("${recovery.plan.service.url:http://localhost:8083}")
    private String recoveryPlanServiceUrl;

    public RecoveryPlanIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Call Recovery Plan Service to create a plan from this appointment
     * 
     * @param appointmentId   - The appointment ID
     * @param userId          - Patient ID from appointment
     * @param counselorId     - Counselor ID from appointment
     * @param appointmentDate - When appointment occurred
     * @param notes           - Optional notes from appointment
     * @return The created recovery plan ID
     */
    public String createRecoveryPlanFromAppointment(String appointmentId, String userId,
            String counselorId, LocalDateTime appointmentDate,
            String notes) {
        try {
            String url = recoveryPlanServiceUrl + "/api/recovery-plans/internal/create-from-appointment";

            AppointmentCompletedEvent event = new AppointmentCompletedEvent(
                    appointmentId,
                    userId,
                    counselorId,
                    appointmentDate,
                    notes);

            PlanCreatedResponse response = restTemplate.postForObject(
                    url,
                    event,
                    PlanCreatedResponse.class);

            if (response != null && response.isSuccess()) {
                System.out.println("Recovery plan created: " + response.getPlanId());
                return response.getPlanId();
            }

            return null;

        } catch (Exception e) {
            System.err.println("Failed to create recovery plan: " + e.getMessage());
            return null;
        }
    }

    public static class AppointmentCompletedEvent {
        private String appointmentId;
        private String patientId;
        private String counselorId;
        private LocalDateTime appointmentDate;
        private String appointmentNotes;

        public AppointmentCompletedEvent(String appointmentId, String patientId, String counselorId,
                LocalDateTime appointmentDate, String appointmentNotes) {
            this.appointmentId = appointmentId;
            this.patientId = patientId;
            this.counselorId = counselorId;
            this.appointmentDate = appointmentDate;
            this.appointmentNotes = appointmentNotes;
        }

        public String getAppointmentId() {
            return appointmentId;
        }

        public void setAppointmentId(String appointmentId) {
            this.appointmentId = appointmentId;
        }

        public String getPatientId() {
            return patientId;
        }

        public void setPatientId(String patientId) {
            this.patientId = patientId;
        }

        public String getCounselorId() {
            return counselorId;
        }

        public void setCounselorId(String counselorId) {
            this.counselorId = counselorId;
        }

        public LocalDateTime getAppointmentDate() {
            return appointmentDate;
        }

        public void setAppointmentDate(LocalDateTime appointmentDate) {
            this.appointmentDate = appointmentDate;
        }

        public String getAppointmentNotes() {
            return appointmentNotes;
        }

        public void setAppointmentNotes(String appointmentNotes) {
            this.appointmentNotes = appointmentNotes;
        }
    }

    public static class PlanCreatedResponse {
        private String planId;
        private String appointmentId;
        private String message;
        private boolean success;

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getAppointmentId() {
            return appointmentId;
        }

        public void setAppointmentId(String appointmentId) {
            this.appointmentId = appointmentId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}