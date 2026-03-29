plugins {
    `java-library`
    alias(libs.plugins.spring.dependency.management)
}

// Publish the classes directory instead of the JAR for intra-repo consumers.
// This means local project(":packages:api-contracts") dependencies resolve to
// build/classes/java/main rather than build/libs/api-contracts.jar.
// Effect: VS Code's JDT never opens the JAR file → no Windows file-lock when
// Gradle needs to delete-and-recreate the artifact during incremental builds.
configurations.named("apiElements") {
    outgoing.artifacts.clear()
    outgoing.artifact(tasks.named<JavaCompile>("compileJava").flatMap { it.destinationDirectory }) {
        builtBy(tasks.named("compileJava"))
    }
}
configurations.named("runtimeElements") {
    outgoing.artifacts.clear()
    outgoing.artifact(tasks.named<JavaCompile>("compileJava").flatMap { it.destinationDirectory }) {
        builtBy(tasks.named("compileJava"))
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}")
    }
}

dependencies {
    api(libs.spring.boot.validation)
    api(libs.jackson.databind)
    api(libs.jackson.jsr310)
    api(libs.jackson.annotations)
}
