package com.jumarkot.observability;

// Placeholder — Phase 2 will add structured MDC logging, trace ID propagation,
// and Micrometer metric helpers here.
public final class ObservabilityConstants {
    public static final String TENANT_ID_MDC_KEY  = "tenantId";
    public static final String DECISION_ID_MDC_KEY = "decisionId";
    public static final String REQUEST_ID_MDC_KEY  = "requestId";

    private ObservabilityConstants() {}
}
