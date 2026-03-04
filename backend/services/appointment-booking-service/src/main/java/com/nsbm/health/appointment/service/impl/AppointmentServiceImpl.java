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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

        if (appointmentRepository.existsByAvailabilityId(request.getAvailabilityId())) {
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

        try {
            Appointment saved = appointmentRepository.save(appointment);
            return AppointmentMapper.toResponse(saved);
        } catch (DuplicateKeyException e) {
            throw new DuplicateBookingException("Slot " + request.getAvailabilityId() + " was taken by a concurrent request.");
        }
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

        // Release the slot so others can book it again
        availabilityClient.releaseSlot(appointment.getAvailabilityId());

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(Instant.now());

        if (request != null && request.getCancelReason() != null && !request.getCancelReason().isBlank()) {
            appointment.setCancelReason(request.getCancelReason());
        }

        return AppointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    /* ---------------------------------------------------------
       RESCHEDULE APPOINTMENT
       - Releases old slot
       - Books new slot
       - Updates appointment with new slot details
    ---------------------------------------------------------- */
    @Override
    public AppointmentResponse rescheduleAppointment(String appointmentId, RescheduleAppointmentRequest request) {
        log.info("Rescheduling appointment - id={}, newAvailabilityId={}", appointmentId, request.getNewAvailabilityId());

        Appointment appointment = findOrThrow(appointmentId);

        if (appointmentRepository.existsByAvailabilityId(request.getNewAvailabilityId())) {
            throw new DuplicateBookingException("New slot " + request.getNewAvailabilityId() + " is already booked.");
        }

        // Release old slot first
        availabilityClient.releaseSlot(appointment.getAvailabilityId());

        // Book new slot
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