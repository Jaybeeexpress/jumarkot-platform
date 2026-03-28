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
    api(libs.spring.boot.actuator)
    api("io.micrometer:micrometer-registry-prometheus")
    api(libs.logstash.encoder)
    implementation(libs.spring.boot.web)
}
