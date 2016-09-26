-- :name insert-instance-status :! :n
INSERT INTO instance_status
    (instance_id)
SELECT
  (:instance-id)
WHERE NOT EXISTS
  (SELECT instance_id
     FROM instance_status
     WHERE instance_id = :instance-id);
