-- :name insert-event :<!
-- :doc Insert an event
INSERT INTO events (payload)
VALUES (:payload::jsonb)
RETURNING *;

-- :name all-events :? :*
-- :doc All events.
SELECT * FROM events;