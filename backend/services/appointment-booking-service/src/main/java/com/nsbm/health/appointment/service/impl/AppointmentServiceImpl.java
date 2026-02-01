package com.nsbm.health.appointment.service.impl;

import com.nsbm.health.appointment.dto.CreateAppointmentRequest;
import com.nsbm.health.appointment.model.Appointment;
import com.nsbm.health.appointment.model.AppointmentStatus;
import com.nsbm.health.appointment.repository.AppointmentRepository;
import com.nsbm.health.appointment.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Appointment create(CreateAppointmentRequest request) {
        Instant now = Instant.now();

        Appointment appointment = new Appointment();
        appointment.setUserId(request.getUserId());
        appointment.setCounselorId(request.getCounselorId());
        appointment.setSlotId(request.getSlotId());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING); // later -> BOOKED after slot reservation
        appointment.setCreatedAt(now);
        appointment.setUpdatedAt(now);

        return appointmentRepository.save(appointment);
    }
    @Override
    public List<Appointment> getByUserId(String userId) {
        return appointmentRepository.findByUserId(userId);
    }

    @Override
    public List<Appointment> getByCounselorId(String counselorId) {
        return appointmentRepository.findByCounselorId(counselorId);
    }

}
