package com.nsbm.health.appointment.controller;

import com.nsbm.health.appointment.client.dto.AvailabilityResponse;
import com.nsbm.health.appointment.dto.AppointmentResponse;
import com.nsbm.health.appointment.dto.BookAppointmentRequest;
import com.nsbm.health.appointment.dto.CancelAppointmentRequest;
import com.nsbm.health.appointment.dto.RescheduleAppointmentRequest;
import com.nsbm.health.appointment.service.AppointmentService;
import com.nsbm.health.appointment.util.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for appointment booking operations.
 * Base path: /api/v1/appointments
 */
@RestController
@RequestMapping(ApiPaths.APPOINTMENTS)
@Tag(name = "Appointments", description = "Book, cancel, reschedule and retrieve appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Book a new appointment")
    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.bookAppointment(request));
    }

    @Operation(summary = "Cancel an appointment")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable("id") String appointmentId,
            @RequestBody(required = false) CancelAppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId, request));
    }

    @Operation(summary = "Reschedule an appointment to a new slot")
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @PathVariable("id") String appointmentId,
            @Valid @RequestBody RescheduleAppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(appointmentId, request));
    }

    @Operation(summary = "Get appointments by userId or counselorId")
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAppointments(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String counselorId) {

        if (userId == null && counselorId == null) {
            throw new IllegalArgumentException("Either userId or counselorId must be provided.");
        }

        if (userId != null) {
            return ResponseEntity.ok(appointmentService.getAppointmentsByUserId(userId));
        }
        return ResponseEntity.ok(appointmentService.getAppointmentsByCounselorId(counselorId));
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailabilityResponse>> getAvailableSlots(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                appointmentService.getAvailableSlotsByDate(date)
        );
    }
}