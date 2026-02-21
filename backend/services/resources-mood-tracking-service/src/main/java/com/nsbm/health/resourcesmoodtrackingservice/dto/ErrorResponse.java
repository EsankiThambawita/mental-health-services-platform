package com.nsbm.health.resourcesmoodtrackingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response for API errors
 * Sent when something goes wrong with a request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response containing details about the error")
public class ErrorResponse {

    // When the error occurred
    @Schema(description = "Timestamp when the error occurred")
    private String timestamp;

    // HTTP status code (400, 404, 500, etc)
    @Schema(description = "HTTP status code")
    private int status;

    // Error type (Bad Request, Not Found, etc)
    @Schema(description = "Error type or error code")
    private String error;

    // Description of what went wrong
    @Schema(description = "Error message describing what went wrong")
    private String message;

    // The URL path where the error occurred
    @Schema(description = "The request path where the error occurred")
    private String path;
}

