-- :name create-table-events :! :raw
-- :doc Create the events table
CREATE TABLE IF NOT EXISTS events (
    id serial,
    payload jsonb,
    created_at timestamptz DEFAULT now()
);
