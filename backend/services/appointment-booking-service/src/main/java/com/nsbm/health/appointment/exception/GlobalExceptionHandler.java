package com.nsbm.health.appointment.exception;

import com.nsbm.health.appointment.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Catches all exceptions thrown by controllers and services,
 * converts them into the standard ApiErrorResponse format.
 * Internal stack traces are never exposed to API consumers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(AppointmentNotFoundException ex, HttpServletRequest req) {
        log.warn("Not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(SlotNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSlotNotFound(SlotNotFoundException ex, HttpServletRequest req) {
        log.warn("Slot not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Slot Not Found", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(SlotNotAvailableException.class)
    public ResponseEntity<ApiErrorResponse> handleSlotNotAvailable(SlotNotAvailableException ex, HttpServletRequest req) {
        log.warn("Slot not available: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Slot Not Available", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(DuplicateBookingException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateBookingException ex, HttpServletRequest req) {
        log.warn("Duplicate booking: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Duplicate Booking", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(AvailabilityServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleAvailabilityDown(AvailabilityServiceException ex, HttpServletRequest req) {
        log.error("Availability service error: {}", ex.getMessage());
        return build(HttpStatus.SERVICE_UNAVAILABLE, "Availability Service Unavailable", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Validation Failed", message, req.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.", req.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String error, String message, String path) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(status.value(), error, message, path));
    }
}