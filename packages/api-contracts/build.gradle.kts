plugins {
    `java-library`
}

description = "Jumarkot API Contracts – shared DTOs and request/response types"

dependencies {
    // Serialization
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)

    // Validation
    api(libs.jakarta.validation.api)

    // Lombok (compile-time only)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // Testing
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Built-By" to "Jumarkot Platform"
        )
    }
}
