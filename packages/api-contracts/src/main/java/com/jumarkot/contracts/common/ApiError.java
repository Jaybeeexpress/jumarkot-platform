package com.jumarkot.contracts.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Standard error response envelope returned by all Jumarkot API endpoints when a request
 * cannot be fulfilled.
 *
 * <p>All 4xx and 5xx responses from Jumarkot services are serialised into this structure,
 * enabling clients to handle errors uniformly regardless of the originating service.
 *
 * <pre>{@code
 * {
 *   "code": "VALIDATION_FAILED",
 *   "message": "Request validation failed",
 *   "traceId": "01j2k3m4n5p6q7r8s9t0",
 *   "details": [
 *     { "field": "entityId", "issue": "must not be blank" }
 *   ],
 *   "timestamp": "2024-07-01T10:00:00Z"
 * }
 * }</pre>
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    /**
     * Machine-readable error code. Follows the pattern {@code SCREAMING_SNAKE_CASE}.
     * Stable across releases; suitable for programmatic error handling.
     */
    private final String code;

    /**
     * Human-readable summary of the error. Should be descriptive but must not include
     * sensitive data (PII, credentials, internal stack details).
     */
    private final String message;

    /**
     * Distributed tracing identifier propagated from the incoming request via
     * {@code X-Trace-Id} / W3C {@code traceparent} header. Useful for correlating
     * errors across service logs.
     */
    private final String traceId;

    /**
     * UTC timestamp at which the error was generated.
     */
    private final Instant timestamp;

    /**
     * Optional list of field-level or item-level error details.
     * Present on validation errors (HTTP 422) and batch operation failures.
     */
    private final List<ErrorDetail> details;

    // ── Nested detail record ─────────────────────────────────────────────────

    /**
     * Describes a single field-level or item-level error within an {@link ApiError}.
     */
    @Data
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {

        /**
         * JSON Pointer (RFC 6901) path to the offending field relative to the request body,
         * e.g. {@code "/context/amount"}, or an item identifier for batch operations.
         */
        private final String field;

        /**
         * Description of the specific constraint violation or issue detected on this field.
         */
        private final String issue;

        /**
         * The rejected value, stringified. Omitted when the value itself is sensitive.
         */
        private final String rejectedValue;
    }
}
