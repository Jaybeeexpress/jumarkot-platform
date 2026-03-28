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
    api(project(":packages:api-contracts"))
    api(libs.spring.boot.web)
    api(libs.spring.boot.security)
    api(libs.spring.boot.redis)
    api(libs.jackson.databind)
    implementation(libs.commons.codec)
}
