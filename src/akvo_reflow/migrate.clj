(ns akvo-reflow.migrate
  "Migrates the tenant manager and it's tenants."
  (:require [hugsql.core]))

(hugsql.core/def-db-fns "akvo_reflow/migrate.sql")

(defn migrate [db-uri]
  (create-table-events db-uri))
