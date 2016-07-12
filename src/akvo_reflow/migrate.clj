(ns akvo-reflow.migrate
  "Migrates the tenant manager and it's tenants."
  (:require [akvo-reflow.utils :refer [with-db-schema]]
            [clojure.java.jdbc :as jdbc]
            [duct.component.ragtime :as ragtime]
            [ragtime.jdbc :as ragtime-jdbc]))

(defn migrate [system]
  (let [ds (select-keys (-> system :db :spec) [:datasource])
        migrations (:ragtime system)]
    (doseq [flow-instance (keys (:flow-config system))]
      (prn (format "Processing: %s" flow-instance))
      (jdbc/execute! ds (format "CREATE SCHEMA IF NOT EXISTS \"%s\"" flow-instance))
      (with-db-schema [conn ds] flow-instance
        (ragtime/migrate (assoc migrations :datastore (ragtime-jdbc/sql-database conn)))))))

(defn rollback [system]
  )
