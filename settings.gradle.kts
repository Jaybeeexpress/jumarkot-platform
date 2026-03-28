pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "jumarkot-platform"

// Packages
include(":packages:api-contracts")
include(":packages:event-schemas")
include(":packages:shared-auth")
include(":packages:shared-observability")
include(":packages:shared-testkit")

// Services
include(":services:identity-access-service")
include(":services:tenant-service")
include(":services:billing-entitlements-service")
include(":services:event-ingestion-service")
include(":services:decision-engine-service")
include(":services:rules-service")
include(":services:entity-profile-service")
include(":services:alert-case-service")
include(":services:developer-platform-service")
