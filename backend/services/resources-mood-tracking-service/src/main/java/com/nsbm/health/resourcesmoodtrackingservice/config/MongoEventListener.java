package com.nsbm.health.resourcesmoodtrackingservice.config;

import com.nsbm.health.resourcesmoodtrackingservice.model.MoodEntry;
import com.nsbm.health.resourcesmoodtrackingservice.model.Resource;
import com.nsbm.health.resourcesmoodtrackingservice.model.ResourceCategory;
import com.nsbm.health.resourcesmoodtrackingservice.model.MoodSummary;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

/**
 * MongoDB Event Listener for automatic timestamp management
 */
@Component
public class MongoEventListener extends AbstractMongoEventListener<Object> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();

        if (source instanceof MoodEntry) {
            ((MoodEntry) source).prePersist();
        } else if (source instanceof Resource) {
            ((Resource) source).prePersist();
        } else if (source instanceof ResourceCategory) {
            ((ResourceCategory) source).prePersist();
        } else if (source instanceof MoodSummary) {
            ((MoodSummary) source).prePersist();
        }
    }
}

