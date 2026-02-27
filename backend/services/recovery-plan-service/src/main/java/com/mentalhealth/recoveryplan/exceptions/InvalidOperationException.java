package com.mentalhealth.recoveryplan.exceptions;

// Thrown when an operation violates logic


public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
