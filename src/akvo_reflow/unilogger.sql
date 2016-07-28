-- :name unprocessed-events :? :*
-- :doc Get all unprocessed events
SELECT id, payload
    FROM events
    WHERE processed = FALSE
    LIMIT :limit;

-- :name set-events-processed :! :n
-- :doc Updates events with ids in the :ids list, setting processed to TRUE
UPDATE events
    SET processed = TRUE
    WHERE id IN (:v*:ids)
