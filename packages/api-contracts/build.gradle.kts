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
    api(libs.spring.boot.validation)
    api(libs.jackson.databind)
    api(libs.jackson.jsr310)
    api(libs.jackson.annotations)
}
