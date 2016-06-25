-- :name insert-event :<!
-- :doc Insert an event
INSERT INTO events (payload)
VALUES (:payload::jsonb)
RETURNING *;

-- :name all-events :? :*
-- :doc All events.
SELECT * FROM events;

-- :name processed-events :? :*
-- :doc Get all processed events
SELECT id, payload
    FROM events
    WHERE processed = TRUE;