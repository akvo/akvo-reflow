
-- :name new-schema :!
CREATE SCHEMA IF NOT EXISTS
  :i:schema-name;

-- :name new-table :!
-- :doc table-name must be schema qualified name "schema.table_name"
CREATE TABLE IF NOT EXISTS :i:table-name
 (id serial PRIMARY KEY,
  created_at bigint NOT NULL,
  payload jsonb NOT NULL);

-- :name new-index :!
-- :doc table-name must be schema qualified name "schema.table_name"
CREATE UNIQUE INDEX IF NOT EXISTS :i:index-name
  ON :i:table-name (md5(payload::text));


-- :name insert-entity :! :n
INSERT
  INTO :i:table-name (created_at, payload)
VALUES (:created-at, :payload)
