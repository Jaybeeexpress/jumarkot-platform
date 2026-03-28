plugins {
    `java-library`
}

dependencies {
    api(libs.spring.kafka)
    api("com.fasterxml.jackson.core:jackson-databind")
}
