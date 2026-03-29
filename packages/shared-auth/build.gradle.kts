plugins {
    `java-library`
    alias(libs.plugins.spring.dependency.management)
}

// Publish the classes directory instead of the JAR for intra-repo consumers.
// Same rationale as packages/api-contracts — prevents Windows JAR file-lock.
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
    api(project(":packages:api-contracts"))
    api(libs.spring.boot.web)
    api(libs.spring.boot.security)
    api(libs.spring.boot.redis)
    api(libs.jackson.databind)
    implementation(libs.commons.codec)

    testImplementation(libs.spring.boot.test)
}
