package com.nsbm.health.appointment.dto;

public class CreateAppointmentRequest {
    private String userId;
    private String counselorId;
    private String slotId;
    private String notes;

    public CreateAppointmentRequest() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCounselorId() { return counselorId; }
    public void setCounselorId(String counselorId) { this.counselorId = counselorId; }

    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
