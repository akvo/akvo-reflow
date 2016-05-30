-- :name insert-event :<!
-- :doc Insert an event
INSERT INTO events (payload)
VALUES (:payload::jsonb)
RETURNING *;