package com.jumarkot.contracts.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    String requestId;
    Instant timestamp;
    T data;
    String error;
    String errorCode;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .requestId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .requestId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .errorCode(errorCode)
                .error(message)
                .build();
    }
}
