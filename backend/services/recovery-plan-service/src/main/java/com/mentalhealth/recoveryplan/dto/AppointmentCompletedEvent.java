package com.mentalhealth.recoveryplan.dto;

import java.time.LocalDateTime;

public class AppointmentCompletedEvent {

    private String appointmentId;
    private String patientId;
    private String counselorId;
    private LocalDateTime appointmentDate;
    private String appointmentNotes; // Notes from the appointment

    public AppointmentCompletedEvent() {
    }

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