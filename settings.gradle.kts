rootProject.name = "jumarkot-platform"

// Shared packages
include("packages:api-contracts")
include("packages:event-schemas")

// Backend services
include("services:identity-access-service")
include("services:tenant-service")
include("services:billing-entitlements-service")
include("services:event-ingestion-service")
include("services:decision-engine-service")
include("services:rules-service")
include("services:entity-profile-service")
include("services:alert-case-service")
include("services:developer-platform-service")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
