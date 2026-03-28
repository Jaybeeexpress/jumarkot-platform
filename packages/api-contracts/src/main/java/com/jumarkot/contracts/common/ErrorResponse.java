package com.jumarkot.contracts.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Standard envelope for API error responses.
 * Used by global exception handlers across all services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean success,
        String errorCode,
        String message,
        List<FieldError> fieldErrors,
        String requestId,
        Instant timestamp
) {
    public record FieldError(String field, String message) {}

    public static ErrorResponse of(String errorCode, String message, String requestId) {
        return new ErrorResponse(false, errorCode, message, null, requestId, Instant.now());
    }

    public static ErrorResponse withFieldErrors(String errorCode, String message,
                                                List<FieldError> fieldErrors, String requestId) {
        return new ErrorResponse(false, errorCode, message, fieldErrors, requestId, Instant.now());
    }
}
