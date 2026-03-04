package com.nsbm.health.appointment.repository;

import com.nsbm.health.appointment.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for Appointment documents.
 * The unique index on availabilityId is declared on the entity
 * and enforced at DB level to prevent double-booking.
 */
@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByUserName(String userName);


    boolean existsByAvailabilityId(String availabilityId);
}