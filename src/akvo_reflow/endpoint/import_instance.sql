-- :name instance-status :? :1
-- :doc get status fields for named instance
SELECT import_done, export_done
    FROM public.instance_status
    WHERE instance_id = :instance_id;

-- :name set-import-done :! :n
-- :doc Updates import_done for :instance_id to TRUE
UPDATE public.instance_status
    SET import_done = TRUE
    WHERE instance_id = :instance_id;
