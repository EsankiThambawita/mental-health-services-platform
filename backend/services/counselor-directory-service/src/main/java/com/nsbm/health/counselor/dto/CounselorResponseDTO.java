package com.nsbm.health.counselor.dto;

import com.nsbm.health.counselor.model.Specialization;
import java.util.List;

public class CounselorResponseDTO {

    private String id;
    private String name;
    private List<Specialization> specializations;
    private List<String> languages;
    private int experienceYears;

    // Getter and Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for specializations
    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<Specialization> specializations) {
        this.specializations = specializations;
    }

    // Getter and Setter for languages
    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    // Getter and Setter for experienceYears
    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
}

