package com.nsbm.health.appointment.repository;

import com.nsbm.health.appointment.model.Appointment;
import com.nsbm.health.appointment.model.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for Appointment documents.
 * Active-booking uniqueness is enforced at application level
 * by checking existsByAvailabilityIdAndStatusNot(id, CANCELLED).
 */
@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByUserName(String userName);

    boolean existsByAvailabilityId(String availabilityId);

    /**
     * Returns true if an appointment exists for this slot that is NOT in the given status.
     * Used to allow re-booking of cancelled slots while preventing double-booking of active ones.
     */
    boolean existsByAvailabilityIdAndStatusNot(String availabilityId, AppointmentStatus status);
}