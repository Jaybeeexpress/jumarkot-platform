rootProject.name = "jumarkot-platform"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://packages.confluent.io/maven/") }
    }
}

// ── Shared Packages ───────────────────────────────────────────────────────────
include(":packages:api-contracts")
include(":packages:event-schemas")

// ── Platform Services (to be added as scaffolded) ────────────────────────────
include(":services:identity-access-service")
// include(":services:iam-service")
include(":services:tenant-service")
// include(":services:event-ingestion-service")
// include(":services:decision-engine-service")
// include(":services:rules-engine-service")
// include(":services:case-management-service")
// include(":services:audit-service")
// include(":services:notification-service")
// include(":services:api-gateway")
