package com.jumarkot.contracts.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standard envelope for all successful API responses.
 * {@code data} carries the domain payload; {@code meta} is optional.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String requestId,
        Instant timestamp
) {
    public static <T> ApiResponse<T> ok(T data, String requestId) {
        return new ApiResponse<>(true, data, requestId, Instant.now());
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, null);
    }
}
