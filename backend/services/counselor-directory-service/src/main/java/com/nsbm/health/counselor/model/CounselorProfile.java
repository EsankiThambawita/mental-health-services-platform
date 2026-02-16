package com.nsbm.health.counselor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
// todo - change the health.appointmrnt to counselar

@Document(collection = "counselors")
public class CounselorProfile {

    @Id
    private String id;

    private String name;
    private String specialization;
    private String language;
    private int experienceYears;
    private String availabilitySummary;

    // Constructors
    public CounselorProfile() {}

    // Getters & Setters
}