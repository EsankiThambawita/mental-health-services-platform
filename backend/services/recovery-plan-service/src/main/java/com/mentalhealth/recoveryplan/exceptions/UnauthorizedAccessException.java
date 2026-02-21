package com.mentalhealth.recoveryplan.exceptions;

// Thrown when a user tried to access a resource they dont have authorisation for


public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
