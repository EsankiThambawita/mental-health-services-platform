/* =========================================================
   AppointmentServiceImpl.java
   - Business logic for appointment booking
   - Key rules:
     1) Book -> calls availabilityClient.bookSlot() then saves appointment
     2) Cancel -> calls availabilityClient.releaseSlot(oldSlot) then marks CANCELLED
     3) Reschedule -> release old slot -> book new slot -> update appointment
   - Stores userName ONLY (no userId)
   ========================================================= */
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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

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

    /* ---------------------------------------------------------
       BOOK APPOINTMENT
       - Ensures we don't double-book by availabilityId
       - Locks slot in availability service
       - Saves appointment in Mongo
    ---------------------------------------------------------- */
    @Override
    public AppointmentResponse bookAppointment(BookAppointmentRequest request) {
        log.info("Booking appointment - userName={}, availabilityId={}", request.getUserName(), request.getAvailabilityId());

        if (appointmentRepository.existsByAvailabilityIdAndStatusNot(request.getAvailabilityId(), AppointmentStatus.CANCELLED)) {
            throw new DuplicateBookingException("Slot " + request.getAvailabilityId() + " is already booked.");
        }

        AvailabilityResponse slot = availabilityClient.bookSlot(request.getAvailabilityId());

        Appointment appointment = new Appointment(
                slot.getAvailabilityId(),
                request.getUserName(),
                slot.getCounselorId(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                AppointmentStatus.CONFIRMED
        );
        appointment.setCreatedAt(Instant.now());
        appointment.setUpdatedAt(Instant.now());

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentMapper.toResponse(saved);
    }

    /* ---------------------------------------------------------
       CANCEL APPOINTMENT
       - Releases slot in availability service
       - Marks appointment CANCELLED
    ---------------------------------------------------------- */
    @Override
    public AppointmentResponse cancelAppointment(String appointmentId, CancelAppointmentRequest request) {
        log.info("Cancelling appointment - id={}", appointmentId);

        Appointment appointment = findOrThrow(appointmentId);

        // Cannot cancel an already cancelled appointment
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Appointment is already cancelled.");
        }

        // Release the slot so others can book it again (best-effort)
        try {
            availabilityClient.releaseSlot(appointment.getAvailabilityId());
        } catch (Exception releaseEx) {
            log.warn("Could not release slot {} (may already be released): {}", appointment.getAvailabilityId(), releaseEx.getMessage());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(Instant.now());

        if (request != null && request.getCancelReason() != null && !request.getCancelReason().isBlank()) {
            appointment.setCancelReason(request.getCancelReason());
        }

        return AppointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    /* ---------------------------------------------------------
       RESCHEDULE APPOINTMENT
       - Validates appointment is not cancelled
       - Releases old slot
       - Books new slot (with rollback on failure)
       - Updates appointment with new slot details
    ---------------------------------------------------------- */
    @Override
    public AppointmentResponse rescheduleAppointment(String appointmentId, RescheduleAppointmentRequest request) {
        log.info("Rescheduling appointment - id={}, newAvailabilityId={}", appointmentId, request.getNewAvailabilityId());

        Appointment appointment = findOrThrow(appointmentId);

        // Cannot reschedule a cancelled appointment
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot reschedule a cancelled appointment. Please book a new one.");
        }

        String oldAvailabilityId = appointment.getAvailabilityId();

        // Cannot reschedule to the same slot
        if (oldAvailabilityId.equals(request.getNewAvailabilityId())) {
            throw new IllegalArgumentException("New slot is the same as the current slot.");
        }

        if (appointmentRepository.existsByAvailabilityIdAndStatusNot(request.getNewAvailabilityId(), AppointmentStatus.CANCELLED)) {
            throw new DuplicateBookingException("New slot " + request.getNewAvailabilityId() + " is already booked.");
        }

        // Release old slot first (best-effort: if already released, just log and continue)
        try {
            availabilityClient.releaseSlot(oldAvailabilityId);
        } catch (Exception releaseEx) {
            log.warn("Could not release old slot {} (may already be released): {}", oldAvailabilityId, releaseEx.getMessage());
        }

        // Book new slot — if this fails, rollback by re-booking the old slot
        AvailabilityResponse newSlot;
        try {
            newSlot = availabilityClient.bookSlot(request.getNewAvailabilityId());
        } catch (Exception e) {
            log.error("Failed to book new slot, rolling back old slot release: {}", e.getMessage());
            try {
                availabilityClient.bookSlot(oldAvailabilityId);
            } catch (Exception rollbackEx) {
                log.error("Rollback also failed for old slot {}: {}", oldAvailabilityId, rollbackEx.getMessage());
            }
            throw e;
        }

        appointment.setAvailabilityId(newSlot.getAvailabilityId());
        appointment.setCounselorId(newSlot.getCounselorId());
        appointment.setDate(newSlot.getDate());
        appointment.setStartTime(newSlot.getStartTime());
        appointment.setEndTime(newSlot.getEndTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointment.setUpdatedAt(Instant.now());

        return AppointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    /* ---------------------------------------------------------
       QUERY: BY userName
    ---------------------------------------------------------- */
    @Override
    public List<AppointmentResponse> getAppointmentsByUserName(String userName) {
        return appointmentRepository.findByUserName(userName)
                .stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }


    /* ---------------------------------------------------------
       PROXY: available slots by date
       - calls availability service
    ---------------------------------------------------------- */
    @Override
    public List<AvailabilityResponse> getAvailableSlotsByDate(LocalDate date) {
        return availabilityClient.getAvailableSlotsByDate(date);
    }

    /* ---------------------------------------------------------
       Helper: find appointment or throw 404-style exception
    ---------------------------------------------------------- */
    private Appointment findOrThrow(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found: " + id));
    }
}