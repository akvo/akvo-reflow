-- :name create-table-events :! :raw
-- :doc Create the events table
CREATE TABLE IF NOT EXISTS events (
    id serial,
    payload jsonb,
    created_at timestamptz DEFAULT now()
);

-- :name drop-table-events :! :raw
-- :doc Drop the events table
DROP TABLE IF EXISTS events;
