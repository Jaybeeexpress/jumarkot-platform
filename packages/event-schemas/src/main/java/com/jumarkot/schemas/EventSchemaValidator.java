package com.jumarkot.schemas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe validator that checks event payloads against their registered JSON Schemas.
 *
 * <p>Schemas are loaded from the classpath ({@code schemas/events/<type>.json}) and
 * cached after first load to avoid repeated I/O. The validator follows JSON Schema
 * draft-07 using the {@code networknt/json-schema-validator} library.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * EventSchemaValidator validator = new EventSchemaValidator();
 *
 * ValidationResult result = validator.validate("transaction.completed", payloadMap);
 * if (!result.isValid()) {
 *     result.getErrors().forEach(e -> log.warn("Validation error: {}", e));
 * }
 * }</pre>
 */
public class EventSchemaValidator {

    private static final String SCHEMA_RESOURCE_PATH = "schemas/events/";

    /** Maps canonical event type name to the corresponding event schema file name. */
    private static final Map<String, String> EVENT_TYPE_TO_SCHEMA_FILE = Map.of(
            "transaction.completed", "transaction.event.json",
            "transaction.initiated", "transaction.event.json",
            "transaction.failed", "transaction.event.json",
            "user.login", "login.event.json",
            "user.login.failed", "login.event.json",
            "user.onboarding.initiated", "onboarding.event.json",
            "user.onboarding.stage.updated", "onboarding.event.json",
            "user.onboarding.completed", "onboarding.event.json"
    );

    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory;
    private final Map<String, JsonSchema> schemaCache = new ConcurrentHashMap<>();

    public EventSchemaValidator() {
        this(new ObjectMapper());
    }

    public EventSchemaValidator(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setFailFast(false);
        this.schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    }

    /**
     * Validates an event payload against the JSON Schema registered for the given event type.
     *
     * @param eventType the canonical event type string, e.g. {@code "transaction.completed"}
     * @param payload   the event payload as a raw {@link Map}
     * @return a {@link ValidationResult} describing whether validation passed and any errors
     * @throws IllegalArgumentException if {@code eventType} has no registered schema
     * @throws EventSchemaException     if the schema cannot be loaded from the classpath
     */
    public ValidationResult validate(String eventType, Map<String, Object> payload) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(payload, "payload must not be null");

        JsonSchema schema = resolveSchema(eventType);
        JsonNode payloadNode = objectMapper.valueToTree(payload);
        Set<ValidationMessage> errors = schema.validate(payloadNode);

        return new ValidationResult(eventType, errors);
    }

    /**
     * Validates a pre-parsed JSON node directly.
     *
     * @param eventType   the canonical event type string
     * @param payloadNode the event payload as a {@link JsonNode}
     * @return a {@link ValidationResult}
     */
    public ValidationResult validate(String eventType, JsonNode payloadNode) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(payloadNode, "payloadNode must not be null");

        JsonSchema schema = resolveSchema(eventType);
        Set<ValidationMessage> errors = schema.validate(payloadNode);
        return new ValidationResult(eventType, errors);
    }

    /**
     * Returns {@code true} if the given event type has a registered schema.
     *
     * @param eventType the event type to check
     * @return {@code true} if a schema exists for this event type
     */
    public boolean hasSchema(String eventType) {
        return EVENT_TYPE_TO_SCHEMA_FILE.containsKey(eventType);
    }

    private JsonSchema resolveSchema(String eventType) {
        String schemaFile = EVENT_TYPE_TO_SCHEMA_FILE.get(eventType);
        if (schemaFile == null) {
            throw new IllegalArgumentException(
                    "No JSON schema registered for event type: " + eventType
                            + ". Registered types: " + EVENT_TYPE_TO_SCHEMA_FILE.keySet());
        }

        return schemaCache.computeIfAbsent(schemaFile, this::loadSchema);
    }

    private JsonSchema loadSchema(String schemaFile) {
        String resourcePath = SCHEMA_RESOURCE_PATH + schemaFile;
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new EventSchemaException(
                    "Schema resource not found on classpath: " + resourcePath);
        }

        try {
            JsonNode schemaNode = objectMapper.readTree(stream);
            return schemaFactory.getSchema(schemaNode);
        } catch (IOException e) {
            throw new EventSchemaException("Failed to parse schema: " + schemaFile, e);
        }
    }

    // ── Inner types ───────────────────────────────────────────────────────────

    /**
     * Immutable result of a schema validation operation.
     */
    public static final class ValidationResult {

        private final String eventType;
        private final Set<ValidationMessage> errors;

        private ValidationResult(String eventType, Set<ValidationMessage> errors) {
            this.eventType = eventType;
            this.errors = Set.copyOf(errors);
        }

        /** Returns {@code true} if the payload passed all schema constraints. */
        public boolean isValid() {
            return errors.isEmpty();
        }

        /** Returns the event type that was validated. */
        public String getEventType() {
            return eventType;
        }

        /**
         * Returns an immutable set of {@link ValidationMessage} instances describing
         * each schema violation. Empty when {@link #isValid()} is {@code true}.
         */
        public Set<ValidationMessage> getErrors() {
            return errors;
        }

        @Override
        public String toString() {
            return "ValidationResult{eventType='" + eventType + "', valid=" + isValid()
                    + ", errorCount=" + errors.size() + "}";
        }
    }

    /**
     * Unchecked exception thrown when a schema cannot be loaded or parsed.
     */
    public static final class EventSchemaException extends RuntimeException {

        public EventSchemaException(String message) {
            super(message);
        }

        public EventSchemaException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
