-- :name unprocessed-events :? :*
-- :doc Get all unprocessed events
SELECT id, payload
    FROM events
    WHERE processed = FALSE;

-- :name set-event-processed :! :n
-- :doc Updates an event, setting processed to TRUE
UPDATE events
    SET processed = TRUE
    WHERE id = :id
