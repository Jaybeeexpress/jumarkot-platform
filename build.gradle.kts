import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.4.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.flywaydb.flyway") version "10.21.0" apply false
    id("nu.studer.jooq") version "9.0" apply false
}

allprojects {
    group = "com.jumarkot"
    version = "0.1.0-SNAPSHOT"
}

// Apply Java + Spring conventions to every service subproject
subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // Suppress the executable-jar task for library / shared modules
    tasks.withType<BootJar> {
        enabled = false
    }
}
