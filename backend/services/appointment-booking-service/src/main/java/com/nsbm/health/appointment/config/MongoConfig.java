package com.nsbm.health.appointment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/** Enables @CreatedDate and @LastModifiedDate auditing on Appointment entity. */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}