plugins {
    `java-library`
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}")
    }
}

dependencies {
    api(libs.spring.boot.test)
    api(libs.testcontainers.junit)
    api(libs.testcontainers.pg)
    api(libs.testcontainers.kafka)
    api("com.redis:testcontainers-redis:2.2.2")
}
