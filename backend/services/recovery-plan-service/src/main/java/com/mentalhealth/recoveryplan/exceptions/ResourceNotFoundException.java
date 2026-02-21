package com.mentalhealth.recoveryplan.exceptions;

// Thrown when a requested resource doesn't exist in the database

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s", resourceName, fieldName, fieldValue));
    }
}
