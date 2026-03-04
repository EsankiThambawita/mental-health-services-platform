/* =========================================================
   AvailabilityRepositoryImpl.java (Availability Service)
   - Mongo atomic updates (findAndModify)
   - bookIfAvailable:
       AVAILABLE -> BOOKED
   - releaseIfBooked (NEW):
       BOOKED -> AVAILABLE
   ========================================================= */
package com.nsbm.health.availability.repository;

import com.nsbm.health.availability.model.AvailabilitySlot;
import com.nsbm.health.availability.model.AvailabilityStatus;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Optional;

public class AvailabilityRepositoryImpl implements AvailabilityRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public AvailabilityRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /* ---------------------------------------------------------
       BOOK IF AVAILABLE
       - Only succeeds if slot exists AND status == AVAILABLE
       - Returns updated document if booked, else empty
    ---------------------------------------------------------- */
    @Override
    public Optional<AvailabilitySlot> bookIfAvailable(String availabilityId) {
        Query query = new Query(
                Criteria.where("_id").is(availabilityId)
                        .and("status").is(AvailabilityStatus.AVAILABLE)
        );

        Update update = new Update().set("status", AvailabilityStatus.BOOKED);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        AvailabilitySlot updated = mongoTemplate.findAndModify(query, update, options, AvailabilitySlot.class);
        return Optional.ofNullable(updated);
    }

    /* ---------------------------------------------------------
       RELEASE IF BOOKED (NEW)
       - Only succeeds if slot exists AND status == BOOKED
       - Returns updated document if released, else empty
    ---------------------------------------------------------- */
    @Override
    public Optional<AvailabilitySlot> releaseIfBooked(String availabilityId) {
        Query query = new Query(
                Criteria.where("_id").is(availabilityId)
                        .and("status").is(AvailabilityStatus.BOOKED)
        );

        Update update = new Update().set("status", AvailabilityStatus.AVAILABLE);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        AvailabilitySlot updated = mongoTemplate.findAndModify(query, update, options, AvailabilitySlot.class);
        return Optional.ofNullable(updated);
    }
}