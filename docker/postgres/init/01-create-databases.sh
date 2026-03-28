#!/bin/bash
# docker/postgres/init/01-create-databases.sh
# Creates all Jumarkot service databases and grants privileges to the platform user.
# This script is automatically executed by the postgres container on first boot.
set -euo pipefail

POSTGRES_USER="${POSTGRES_USER:-jumarkot}"

databases=(
  "jumarkot_iam"
  "jumarkot_tenants"
  "jumarkot_events"
  "jumarkot_decisions"
  "jumarkot_rules"
  "jumarkot_cases"
)

for db in "${databases[@]}"; do
  echo "Creating database: $db"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-SQL
    CREATE DATABASE $db
      WITH OWNER = $POSTGRES_USER
           ENCODING = 'UTF8'
           LC_COLLATE = 'en_US.utf8'
           LC_CTYPE = 'en_US.utf8'
           TEMPLATE = template0
           CONNECTION LIMIT = -1;
    COMMENT ON DATABASE $db IS 'Jumarkot Platform – $db';
SQL
done

echo "All Jumarkot databases created successfully."
