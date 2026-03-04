package com.nsbm.health.appointment.service.impl;

import com.nsbm.health.appointment.client.AvailabilityClient;
import com.nsbm.health.appointment.client.dto.AvailabilityResponse;
import com.nsbm.health.appointment.dto.AppointmentResponse;
import com.nsbm.health.appointment.dto.BookAppointmentRequest;
import com.nsbm.health.appointment.dto.CancelAppointmentRequest;
import com.nsbm.health.appointment.dto.RescheduleAppointmentRequest;
import com.nsbm.health.appointment.exception.AppointmentNotFoundException;
import com.nsbm.health.appointment.exception.DuplicateBookingException;
import com.nsbm.health.appointment.model.Appointment;
import com.nsbm.health.appointment.model.AppointmentStatus;
import com.nsbm.health.appointment.repository.AppointmentRepository;
import com.nsbm.health.appointment.service.AppointmentService;
import com.nsbm.health.appointment.util.AppointmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for appointment booking, cancellation, rescheduling and retrieval.
 *
 * Inter-service communication:
 * - Calls AvailabilityClient.bookSlot() which sends PUT /api/v1/availability/{id}/book
 *   to the Availability Management Service running on port 8082.
 * - This is done for both booking and rescheduling to lock the slot.
 * - If the availability service is unreachable, AvailabilityServiceException is thrown
 *   and no appointment is created (returns 503 to client).
 *
 * Double-booking prevention:
 * Layer 1 - existsByAvailabilityId() check before calling availability service.
 * Layer 2 - MongoDB unique index on availabilityId catches concurrent race conditions.
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;
    private final AvailabilityClient availabilityClient;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  AvailabilityClient availabilityClient) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityClient = availabilityClient;
    }

    @Override
    public AppointmentResponse bookAppointment(BookAppointmentRequest request) {
        log.info("Booking appointment - userId={}, availabilityId={}", request.getUserId(), request.getAvailabilityId());

        if (appointmentRepository.existsByAvailabilityId(request.getAvailabilityId())) {
            throw new DuplicateBookingException("Slot " + request.getAvailabilityId() + " is already booked.");
        }

        AvailabilityResponse slot = availabilityClient.bookSlot(request.getAvailabilityId());

        Appointment appointment = new Appointment(
                slot.getAvailabilityId(),
                request.getUserId(),
                slot.getCounselorId(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                AppointmentStatus.CONFIRMED
        );
        appointment.setCreatedAt(Instant.now());
        appointment.setUpdatedAt(Instant.now());

        try {
            Appointment saved = appointmentRepository.save(appointment);
            log.info("Appointment created - id={}", saved.getId());
            return AppointmentMapper.toResponse(saved);
        } catch (DuplicateKeyException e) {
            throw new DuplicateBookingException("Slot " + request.getAvailabilityId() + " was taken by a concurrent request.");
        }
    }

    @Override
    public AppointmentResponse cancelAppointment(String appointmentId, CancelAppointmentRequest request) {
        log.info("Cancelling appointment - id={}", appointmentId);

        Appointment appointment = findOrThrow(appointmentId);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(Instant.now());

        if (request != null && request.getCancelReason() != null && !request.getCancelReason().isBlank()) {
            appointment.setCancelReason(request.getCancelReason());
        }

        return AppointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse rescheduleAppointment(String appointmentId, RescheduleAppointmentRequest request) {
        log.info("Rescheduling appointment - id={}, newAvailabilityId={}", appointmentId, request.getNewAvailabilityId());

        Appointment appointment = findOrThrow(appointmentId);

        if (appointmentRepository.existsByAvailabilityId(request.getNewAvailabilityId())) {
            throw new DuplicateBookingException("New slot " + request.getNewAvailabilityId() + " is already booked.");
        }

        AvailabilityResponse newSlot = availabilityClient.bookSlot(request.getNewAvailabilityId());

        appointment.setAvailabilityId(newSlot.getAvailabilityId());
        appointment.setCounselorId(newSlot.getCounselorId());
        appointment.setDate(newSlot.getDate());
        appointment.setStartTime(newSlot.getStartTime());
        appointment.setEndTime(newSlot.getEndTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointment.setUpdatedAt(Instant.now());

        try {
            return AppointmentMapper.toResponse(appointmentRepository.save(appointment));
        } catch (DuplicateKeyException e) {
            throw new DuplicateBookingException("New slot " + request.getNewAvailabilityId() + " was taken by a concurrent request.");
        }
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByUserId(String userId) {
        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(AppointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByCounselorId(String counselorId) {
        return appointmentRepository.findByCounselorId(counselorId)
                .stream()
                .map(AppointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityResponse> getAvailableSlotsByDate(LocalDate date) {
        return availabilityClient.getAvailableSlotsByDate(date);
    }

    private Appointment findOrThrow(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found: " + id));
    }

}