package com.jumarkot.testkit;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

// Shared Testcontainers singletons — reuse across test suites to avoid per-test startup cost.
// Phase 2: add Kafka container and Spring Boot test base classes.
public final class TestContainers {

    public static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    public static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);

    static {
        POSTGRES.start();
        REDIS.start();
    }

    private TestContainers() {}
}
