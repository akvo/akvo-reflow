-- :name insert-entity :! :n
INSERT
  INTO :i:table-name (created_at, payload)
VALUES (:created-at, :payload)


-- :name update-cursor :! :n
-- :doc Updates import_done for :instance_id to TRUE
UPDATE public.instance_status
    SET cursor = :cursor-string,
        kind = :kind
    WHERE instance_id = :instance-id;


-- :name get-cursor :? :1
-- :doc Get kind and cursor of running import
SELECT kind, cursor
    FROM public.instance_status
    WHERE instance_id = :instance-id;
