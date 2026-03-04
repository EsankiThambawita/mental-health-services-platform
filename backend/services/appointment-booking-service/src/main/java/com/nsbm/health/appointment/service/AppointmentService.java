package com.nsbm.health.appointment.service;

import com.nsbm.health.appointment.client.dto.AvailabilityResponse;
import com.nsbm.health.appointment.dto.AppointmentResponse;
import com.nsbm.health.appointment.dto.BookAppointmentRequest;
import com.nsbm.health.appointment.dto.CancelAppointmentRequest;
import com.nsbm.health.appointment.dto.RescheduleAppointmentRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for all appointment business operations.
 * Implemented by AppointmentServiceImpl.
 */
public interface AppointmentService {

    AppointmentResponse bookAppointment(BookAppointmentRequest request);

    AppointmentResponse cancelAppointment(String appointmentId, CancelAppointmentRequest request);

    AppointmentResponse rescheduleAppointment(String appointmentId, RescheduleAppointmentRequest request);

    List<AppointmentResponse> getAppointmentsByUserId(String userId);

    List<AppointmentResponse> getAppointmentsByCounselorId(String counselorId);

    List<AvailabilityResponse> getAvailableSlotsByDate(LocalDate date);
}