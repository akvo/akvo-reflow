-- :name create-table-events :! :raw
-- :doc Create the events table
CREATE TABLE IF NOT EXISTS events (
    id serial PRIMARY KEY,
    payload jsonb NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    processed boolean NOT NULL DEFAULT FALSE
);

-- :name drop-table-events :! :raw
-- :doc Drop the events table
DROP TABLE IF EXISTS events;
