#!/usr/bin/env bash

set -e

CLI_ERR_MSG="Postgres CLI tools not available (psql). Using Postgres.app, look
at http://postgresapp.com/documentation/cli-tools.html. Aborting."
hash psql 2>/dev/null || { echo >&2 $CLI_ERR_MSG ; exit 1; }

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR

# Provision

## Create dash role
psql -c "CREATE ROLE reflow WITH PASSWORD 'password' CREATEDB LOGIN;"

## Create dash dbs
psql -f $DIR/helpers/create-reflow.sql

# Migrate, seed tenant manager with tenants & migrate added tenants
lein do migrate

echo ""
echo "----------"
echo "Done!"
