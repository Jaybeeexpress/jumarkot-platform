package com.jumarkot.iam.web;

import com.jumarkot.contracts.common.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiError.ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> ApiError.ErrorDetail.builder()
                        .field("/" + f.getField().replace('.', '/'))
                        .issue(f.getDefaultMessage())
                        .rejectedValue(f.getRejectedValue() != null ? f.getRejectedValue().toString() : null)
                        .build())
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .code("VALIDATION_FAILED")
                .message("Request validation failed")
                .traceId(request.getHeader("X-Trace-Id"))
                .timestamp(Instant.now())
                .details(details)
                .build();
        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        List<ApiError.ErrorDetail> details = ex.getConstraintViolations().stream()
                .map(cv -> ApiError.ErrorDetail.builder()
                        .field(cv.getPropertyPath().toString())
                        .issue(cv.getMessage())
                        .build())
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .code("VALIDATION_FAILED")
                .message("Constraint violation")
                .traceId(request.getHeader("X-Trace-Id"))
                .timestamp(Instant.now())
                .details(details)
                .build();
        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .code("AUTHENTICATION_FAILED")
                .message(ex.getMessage())
                .traceId(request.getHeader("X-Trace-Id"))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleLocked(
            LockedException ex, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .code("ACCOUNT_LOCKED")
                .message(ex.getMessage())
                .traceId(request.getHeader("X-Trace-Id"))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .code("FORBIDDEN")
                .message("You do not have permission to perform this action")
                .traceId(request.getHeader("X-Trace-Id"))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(
            NoSuchElementException ex, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .traceId(request.getHeader("X-Trace-Id"))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .code("INVALID_REQUEST")
                .message(ex.getMessage())
                .traceId(request.getHeader("X-Trace-Id"))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for request {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ApiError error = ApiError.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .traceId(request.getHeader("X-Trace-Id"))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
