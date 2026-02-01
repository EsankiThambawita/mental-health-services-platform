package com.nsbm.health.appointment.repository;

import com.nsbm.health.appointment.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByUserId(String userId);

    List<Appointment> findByCounselorId(String counselorId);
}
