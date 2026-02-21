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
}