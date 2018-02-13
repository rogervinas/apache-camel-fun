#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
  CREATE TABLE "user_quotes" (
    id int NOT NULL,
    name text NOT NULL,
    quote text NOT NULL
  );

  CREATE TABLE "users" (
    name text NOT NULL,
    description text NOT NULL
  );

  INSERT INTO "users" VALUES ('Ned Stark', 'Lord of Winterfell and Warden of the North');
  INSERT INTO "users" VALUES ('Robert Baratheon', 'King of the Seven Kingdoms');
  INSERT INTO "users" VALUES ('Jaime Lannister', 'Member of the Kingsguard aka Kingslayer');

EOSQL
