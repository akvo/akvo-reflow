-- :name unprocessed-events :? :*
-- :doc Get all unprocessed events
SELECT id, payload
    FROM :i:table-name
    WHERE processed = FALSE
    LIMIT :limit;

-- :name set-events-processed :! :n
-- :doc Updates events with ids in the :ids list, setting processed to TRUE
UPDATE :i:table-name
    SET processed = TRUE
    WHERE id IN (:v*:ids)


-- :name set-export-done :! :n
-- :doc Updates export_done for :instance_id to TRUE
UPDATE public.instance_status
    SET export_done = TRUE
    WHERE instance_id = :instance-id;




-- :name set-export-interrupted :! :n
-- :doc Updates export_done for :instance_id to TRUE
UPDATE public.instance_status
    SET error_status = :error-status,
        error_message = :error-message
    WHERE instance_id = :instance-id;

