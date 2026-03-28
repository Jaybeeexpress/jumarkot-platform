plugins {
    `java-library`
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    api(libs.spring.boot.test)
    api(libs.testcontainers.junit)
    api(libs.testcontainers.postgresql)
    api("org.testcontainers:kafka")
    api("com.redis:testcontainers-redis:2.2.2")
}
