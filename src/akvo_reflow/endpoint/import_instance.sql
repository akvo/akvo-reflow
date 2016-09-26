-- :name instance-status :? :1
-- :doc get status fields for named instance
SELECT import_done, export_done
    FROM public.instance_status
    WHERE instance_id = :instance_id;
