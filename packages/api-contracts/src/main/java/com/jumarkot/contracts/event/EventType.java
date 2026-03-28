package com.jumarkot.contracts.event;

/**
 * Canonical event types supported by the Jumarkot ingestion pipeline.
 * Keep in sync with the event-schemas package and API documentation.
 */
public enum EventType {
    USER_REGISTERED,
    LOGIN_ATTEMPT,
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    PASSWORD_RESET,
    DEVICE_REGISTERED,
    BENEFICIARY_ADDED,
    BENEFICIARY_UPDATED,
    PROFILE_UPDATED,
    TRANSACTION_CREATED,
    TRANSACTION_SETTLED,
    TRANSACTION_FAILED,
    PAYOUT_REQUESTED,
    WITHDRAWAL_REQUESTED,
    APPLICATION_SUBMITTED,
    SESSION_STARTED,
    SESSION_ENDED,
    CARD_ISSUED,
    DOCUMENT_SUBMITTED
}
