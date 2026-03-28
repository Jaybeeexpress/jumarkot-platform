plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    `java-library`
}

description = "Jumarkot Identity & Access Management Service"

dependencies {
    implementation(project(":packages:api-contracts"))

    implementation(libs.bundles.spring.web)
    implementation(libs.bundles.spring.data)
    implementation(libs.bundles.spring.security)
    implementation(libs.bundles.flyway.all)
    implementation(libs.bundles.jackson.all)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.jackson.datatype.jsr310)

    // JWT (RS256)
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // SpringDoc OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // Testing
    testImplementation(libs.bundles.testing.core)
    testImplementation(libs.mockito.junit.jupiter)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("identity-access-service.jar")
}
