-- :name all-instances-status :? :*
-- :doc get status fields for named instance
SELECT instance_id, created_at, import_done, export_done, kind, cursor
    FROM public.instance_status;