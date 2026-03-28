plugins {
    `java-library`
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    api(libs.spring.boot.actuator)
    api("io.micrometer:micrometer-registry-prometheus")
    api(libs.logstash.encoder)
    implementation(libs.spring.boot.web)
}
