# Jumarkot Platform

> **Production-grade multi-tenant Risk, Fraud, and Compliance Intelligence Platform**

Jumarkot is an event-driven, microservices-based platform that provides real-time risk scoring, fraud detection, and compliance screening capabilities for financial services, fintech, and marketplace businesses.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         API Gateway (TLS)                           │
│                  Rate limiting · Auth · Routing                     │
└────────────────────────────┬────────────────────────────────────────┘
                             │
          ┌──────────────────┼──────────────────┐
          ▼                  ▼                  ▼
  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐
  │ IAM Service  │  │Tenant Service│  │Event Ingestion   │
  │ OAuth2/OIDC  │  │Multi-tenancy │  │Service           │
  │ JWT issuance │  │Env mgmt      │  │Schema validation │
  └──────────────┘  └──────────────┘  └────────┬─────────┘
                                               │ Kafka
                             ┌─────────────────┼──────────────────┐
                             ▼                 ▼                  ▼
                    ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐
                    │  Decision    │  │  Rules       │  │  Case Management │
                    │  Engine      │  │  Engine      │  │  Service         │
                    │  ML scoring  │  │  CEL/DSL     │  │  Human review    │
                    └──────────────┘  └──────────────┘  └──────────────────┘
                             │                 │
                    ┌────────┴─────────────────┴────────┐
                    ▼                                   ▼
          ┌──────────────────┐               ┌──────────────────┐
          │  Audit Service   │               │Notification Svc  │
          │  Immutable log   │               │Email/SMS/Webhook │
          └──────────────────┘               └──────────────────┘
```

---

## Services

| Service | Description | Database | Port |
|---|---|---|---|
| `iam-service` | Identity & Access Management – OAuth2/OIDC, JWT issuance, API key management | `jumarkot_iam` | 8080 |
| `tenant-service` | Multi-tenant account lifecycle, environment management, subscription | `jumarkot_tenants` | 8081 |
| `event-ingestion-service` | High-throughput event intake, JSON Schema validation, deduplication, Kafka publish | `jumarkot_events` | 8082 |
| `decision-engine-service` | Real-time risk scoring, ML model inference, decision persistence | `jumarkot_decisions` | 8083 |
| `rules-engine-service` | Rule authoring, versioning, evaluation (CEL/custom DSL), A/B testing | `jumarkot_rules` | 8084 |
| `case-management-service` | Analyst workflow, case creation, evidence collection, resolution | `jumarkot_cases` | 8085 |
| `audit-service` | Immutable, append-only audit log of all decisions, actions, and data changes | `jumarkot_events` | 8086 |
| `notification-service` | Outbound alerts via email, SMS, and webhooks for decisions and case updates | — | 8087 |
| `api-gateway` | TLS termination, rate limiting, request routing, auth enforcement | — | 443 |

---

## Shared Packages

| Package | Description |
|---|---|
| `packages/api-contracts` | Shared Java DTOs, request/response types, enums used across all services |
| `packages/event-schemas` | JSON Schema (draft-07) definitions for all event types + validation utility |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (LTS), Kotlin 1.9 |
| Framework | Spring Boot 3.3.x |
| Build | Gradle 8.7 (Kotlin DSL), version catalog |
| Data access | jOOQ 3.19.x (type-safe SQL), Flyway 10.x (migrations) |
| Database | PostgreSQL 16 |
| Caching | Redis 7 |
| Messaging | Apache Kafka (Confluent Platform 7.6) |
| Schema registry | Confluent Schema Registry |
| Auth | OAuth2 / OIDC (Spring Security), JWT (RS256) |
| Containerisation | Docker, Docker Compose |

---

## Repository Layout

```
jumarkot-platform/
├── build.gradle.kts            # Root Gradle build (shared config)
├── settings.gradle.kts         # Subproject declarations
├── Makefile                    # Developer convenience targets
├── gradle/
│   ├── libs.versions.toml      # Centralised version catalog
│   └── wrapper/                # Gradle wrapper (8.7)
├── packages/
│   ├── api-contracts/          # Shared DTOs and API types
│   └── event-schemas/          # JSON Schemas + EventSchemaValidator
├── services/                   # Microservices (to be added)
│   ├── iam-service/
│   ├── tenant-service/
│   ├── event-ingestion-service/
│   ├── decision-engine-service/
│   ├── rules-engine-service/
│   ├── case-management-service/
│   ├── audit-service/
│   └── notification-service/
└── docker/
    ├── docker-compose.yml      # Full local stack
    ├── .env.example            # Environment variable template
    └── postgres/init/          # DB initialisation scripts
```

---

## Getting Started

### Prerequisites

- **Java 21+** – `java -version`
- **Docker 24+** with Compose v2 – `docker compose version`
- **Make** – `make --version`

### 1. Clone and bootstrap

```bash
git clone https://github.com/your-org/jumarkot-platform.git
cd jumarkot-platform
cp docker/.env.example docker/.env  # Review and adjust values
```

### 2. Start the local infrastructure

```bash
make up
```

This starts PostgreSQL 16 (with all 6 databases pre-created), Redis 7, Kafka, and Schema Registry.

### 3. Build all modules

```bash
make build
```

### 4. Run tests

```bash
make test
```

### 5. Apply code formatting

```bash
make fmt
```

---

## Make Targets

| Target | Description |
|---|---|
| `make up` | Start all Docker services (detached) |
| `make down` | Stop Docker services |
| `make down-v` | Stop Docker services and delete volumes |
| `make build` | Compile and assemble all Gradle subprojects |
| `make test` | Run all unit and integration tests |
| `make fmt` | Apply Spotless code formatting |
| `make check` | Run format checks without modifying files |
| `make clean` | Delete Gradle build outputs |
| `make logs` | Tail Docker service logs |
| `make ps` | Show Docker service status |

---

## Local Infrastructure Ports

| Service | Port |
|---|---|
| PostgreSQL | 5432 |
| Redis | 6379 |
| Kafka | 29092 |
| Schema Registry | 8081 |

---

## Contributing

1. Create a feature branch: `git checkout -b feat/my-feature`
2. Make your changes, run `make fmt && make check && make test`
3. Open a pull request against `main`

All PRs require passing CI (build + test + lint) and at least one approving review.

---

## License

Copyright © 2024 Jumarkot. All rights reserved.
