package com.nsbm.health.counselor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CounselorNotFoundException extends RuntimeException {
    public CounselorNotFoundException(String message) {
        super(message);
    }
}

