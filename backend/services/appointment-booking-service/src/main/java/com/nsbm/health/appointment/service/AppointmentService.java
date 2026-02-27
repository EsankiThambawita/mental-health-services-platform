package com.nsbm.health.appointment.service;

import com.nsbm.health.appointment.dto.CreateAppointmentRequest;
import com.nsbm.health.appointment.model.Appointment;

import java.util.List;

public interface AppointmentService {

    Appointment create(CreateAppointmentRequest request);

    List<Appointment> getByUserId(String userId);

    List<Appointment> getByCounselorId(String counselorId);
}
