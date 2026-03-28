plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":packages:api-contracts"))
    implementation(project(":packages:shared-auth"))

    implementation(libs.spring.boot.web)
    implementation(libs.spring.boot.security)
    implementation(libs.spring.boot.actuator)
    implementation(libs.spring.boot.jooq)
    implementation(libs.spring.boot.redis)
    implementation(libs.spring.boot.validation)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgres)
    implementation(libs.jjwt.api)
    implementation(libs.commons.codec)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    testImplementation(libs.spring.boot.test)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.pg)
    testImplementation(libs.testcontainers.redis)
}
