package com.nsbm.health.availability.repository;

import com.nsbm.health.availability.entity.AvailabilitySlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends MongoRepository<AvailabilitySlot, String> {

    List<AvailabilitySlot> findByCounselorId(String counselorId);

    List<AvailabilitySlot> findByCounselorIdAndIsBookedFalse(String counselorId);

    List<AvailabilitySlot> findByDate(LocalDate date);
}
