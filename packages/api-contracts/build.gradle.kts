plugins {
    java
}

dependencies {
    compileOnly("org.projectlombok:lombok:${rootProject.extra.properties["lombokVersion"] ?: "1.18.36"}")
    annotationProcessor("org.projectlombok:lombok:${rootProject.extra.properties["lombokVersion"] ?: "1.18.36"}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")
}
