package com.nsbm.health.availability.util;

import java.time.LocalTime;

public final class DateTimeUtil {

    private DateTimeUtil(){}

    public static boolean isValidTimeRange(LocalTime start, LocalTime end) {
        return start != null && end != null && start.isBefore(end);
    }
}