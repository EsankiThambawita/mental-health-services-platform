package com.nsbm.health.appointment.util;

import java.lang.reflect.Field;
import java.util.StringJoiner;

/**
 * Generates toString() output reflectively for any object.
 * All model and DTO classes delegate their toString() here
 * instead of maintaining hard-coded field listings.
 * Format: ClassName{field1=value1, field2=value2, ...}
 */
public final class ToStringUtil {

    private ToStringUtil() {
    }

    public static String toString(Object obj) {
        if (obj == null) return "null";

        Class<?> clazz = obj.getClass();
        StringJoiner joiner = new StringJoiner(", ", clazz.getSimpleName() + "{", "}");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                joiner.add(field.getName() + "=" + field.get(obj));
            } catch (IllegalAccessException e) {
                joiner.add(field.getName() + "=<inaccessible>");
            }
        }
        return joiner.toString();
    }
}