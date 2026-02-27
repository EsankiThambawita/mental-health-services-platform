package com.nsbm.health.availability.utils;

public final class AppConstants {

    private AppConstants() {}

    public static final String BASE_API = "/api/v1";
    public static final String AVAILABILITY_API = BASE_API + "/availability";

    public static final String SLOT_ALREADY_BOOKED = "Availability slot is already booked";
    public static final String INVALID_TIME_RANGE = "startTime must be before endTime";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";
}