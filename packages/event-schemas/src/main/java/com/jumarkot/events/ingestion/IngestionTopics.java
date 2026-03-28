package com.jumarkot.events.ingestion;

/** Topic name constants for event ingestion. */
public final class IngestionTopics {
    public static final String RAW_EVENTS   = "jumarkot.events.raw";
    public static final String DEAD_LETTER  = "jumarkot.events.dlq";

    private IngestionTopics() {}
}
