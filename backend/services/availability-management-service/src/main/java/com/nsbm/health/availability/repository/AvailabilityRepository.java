package com.nsbm.health.availability.repository;

import com.nsbm.health.availability.model.AvailabilitySlot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository
        extends MongoRepository<AvailabilitySlot, String>, AvailabilityRepositoryCustom {

    List<AvailabilitySlot> findByCounselorIdAndDateOrderByStartTimeAsc(String counselorId, LocalDate date);
}