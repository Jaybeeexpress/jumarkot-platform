package com.jumarkot.contracts.tenant;

/**
 * Discriminates the deployment tier of a {@link EnvironmentDto}.
 *
 * <p>Each tenant has exactly one {@code PRODUCTION} environment and may have
 * any number of {@code SANDBOX} environments for integration testing and
 * rule authoring without affecting live traffic or compliance records.
 */
public enum EnvironmentType {

    /**
     * Live production environment. Events, decisions, and cases are subject to
     * regulatory retention and audit requirements.
     */
    PRODUCTION,

    /**
     * Isolated sandbox environment. Data is synthetic or anonymised;
     * decisions do not affect real users or regulatory reporting.
     */
    SANDBOX
}
