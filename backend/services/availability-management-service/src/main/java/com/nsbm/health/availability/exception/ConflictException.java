package com.nsbm.health.availability.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseApiException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}