#!/bin/bash
# Creates one PostgreSQL database per Jumarkot service.
# Mounted into Postgres container at /docker-entrypoint-initdb.d/

set -e

DATABASES=(
  identity_db
  tenant_db
  billing_db
  ingestion_db
  decision_db
  rules_db
  entity_db
  case_db
  developer_db
)

for DB in "${DATABASES[@]}"; do
  echo "Creating database: $DB"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE $DB OWNER $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE $DB TO $POSTGRES_USER;
EOSQL
done

echo "All Jumarkot databases created."
