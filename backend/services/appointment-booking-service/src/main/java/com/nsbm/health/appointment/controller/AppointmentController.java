package com.nsbm.health.appointment.controller;

import com.nsbm.health.appointment.dto.CreateAppointmentRequest;
import com.nsbm.health.appointment.model.Appointment;
import com.nsbm.health.appointment.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(@RequestBody CreateAppointmentRequest request) {
        return appointmentService.create(request);
    }

    @GetMapping
    public List<Appointment> getAppointments(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String counselorId
    ) {
        if (userId != null) {
            return appointmentService.getByUserId(userId);
        }
        if (counselorId != null) {
            return appointmentService.getByCounselorId(counselorId);
        }
        throw new IllegalArgumentException("userId or counselorId must be provided");
    }

}
