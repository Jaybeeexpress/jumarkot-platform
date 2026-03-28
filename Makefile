.DEFAULT_GOAL := help

# Determine the docker compose command (v2 plugin vs standalone)
DOCKER_COMPOSE := $(shell docker compose version > /dev/null 2>&1 && echo "docker compose" || echo "docker-compose")

GRADLE      := ./gradlew
DOCKER_DIR  := docker
ENV_FILE    := $(DOCKER_DIR)/.env.example

.PHONY: help up down build test clean logs ps fmt check

## help        Show this help message
help:
	@grep -E '^## ' $(MAKEFILE_LIST) | sed 's/^## /  /' | column -t -s ' '

## up          Start all local infrastructure services (detached)
up:
	@echo "→ Starting Jumarkot local stack…"
	$(DOCKER_COMPOSE) -f $(DOCKER_DIR)/docker-compose.yml --env-file $(ENV_FILE) up -d --remove-orphans
	@echo "✔ Stack is up. Services:"
	@echo "    PostgreSQL    → localhost:5432"
	@echo "    Redis         → localhost:6379"
	@echo "    Kafka         → localhost:29092"
	@echo "    Schema Reg.   → http://localhost:8081"

## down        Stop and remove all local infrastructure services
down:
	@echo "→ Stopping Jumarkot local stack…"
	$(DOCKER_COMPOSE) -f $(DOCKER_DIR)/docker-compose.yml --env-file $(ENV_FILE) down --remove-orphans
	@echo "✔ Stack stopped."

## down-v      Stop services and DELETE all volumes (destructive)
down-v:
	@echo "→ Stopping Jumarkot local stack and removing volumes…"
	$(DOCKER_COMPOSE) -f $(DOCKER_DIR)/docker-compose.yml --env-file $(ENV_FILE) down -v --remove-orphans
	@echo "✔ Stack stopped and volumes removed."

## logs        Tail logs for all docker services (Ctrl+C to stop)
logs:
	$(DOCKER_COMPOSE) -f $(DOCKER_DIR)/docker-compose.yml --env-file $(ENV_FILE) logs -f

## ps          Show status of all docker services
ps:
	$(DOCKER_COMPOSE) -f $(DOCKER_DIR)/docker-compose.yml --env-file $(ENV_FILE) ps

## build       Compile and assemble all Gradle subprojects
build:
	@echo "→ Building all subprojects…"
	$(GRADLE) build -x test
	@echo "✔ Build complete."

## test        Run all unit and integration tests across subprojects
test:
	@echo "→ Running all tests…"
	$(GRADLE) test
	@echo "✔ Tests complete."

## fmt         Apply code formatting (Spotless)
fmt:
	@echo "→ Applying Spotless formatting…"
	$(GRADLE) spotlessApply
	@echo "✔ Formatting applied."

## check       Run lint and format checks without modifying files
check:
	@echo "→ Running Spotless check…"
	$(GRADLE) spotlessCheck
	@echo "✔ Check complete."

## clean       Remove all Gradle build outputs
clean:
	@echo "→ Cleaning Gradle build outputs…"
	$(GRADLE) clean
	@echo "✔ Clean complete."
