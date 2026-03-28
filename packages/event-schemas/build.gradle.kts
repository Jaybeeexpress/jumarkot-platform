plugins {
    `java-library`
}

description = "Jumarkot Event Schemas – JSON Schema definitions and validation utilities"

dependencies {
    // JSON Schema validation
    api(libs.json.schema.validator)
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // Testing
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.processResources {
    filesMatching("schemas/**/*.json") {
        // Preserve JSON schema files as-is
        filter { it }
    }
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
