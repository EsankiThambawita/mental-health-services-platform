package com.mentalhealth.recoveryplan.dto;

public class PlanCreatedResponse {

    private String planId;
    private String appointmentId;
    private String message;
    private boolean success;

    public PlanCreatedResponse() {
    }

    public PlanCreatedResponse(String planId, String appointmentId, String message, boolean success) {
        this.planId = planId;
        this.appointmentId = appointmentId;
        this.message = message;
        this.success = success;
    }

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