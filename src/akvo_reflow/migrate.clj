(ns akvo-reflow.migrate
  "Migrates the tenant manager and it's tenants."
  (:require [akvo-reflow.utils :refer [with-db-schema]]
            [clojure.java.jdbc :as jdbc]
            [duct.component.ragtime :as ragtime]
            [hugsql.core :as hugsql]
            [ragtime.jdbc :as ragtime-jdbc]))

(hugsql/def-db-fns "akvo_reflow/migrate.sql")


(defn migrate-base
  "Create the instance_status table and populate it with one record per instance from flow-config"
  [{:keys [db base-migrations flow-config] :as system}]
  (let [ds (select-keys (:spec db) [:datasource])]
    (ragtime/migrate (assoc base-migrations :datastore (ragtime-jdbc/sql-database ds)))
    (doseq [instance-id (keys @flow-config)]
      (insert-instance-status ds {:instance-id instance-id})))
  system)

(defn migrate-schema
  "Create a schema for each flow instance and populate them with tables to hold entities"
  [{:keys [db schema-migrations flow-config] :as system}]
  (let [ds (select-keys (:spec db) [:datasource])]
    (doseq [flow-instance (keys @flow-config)]
    ;(doseq [flow-instance ["akvoflowsandbox"]]
      (prn (format "Processing: %s" flow-instance))
      (jdbc/execute! ds (format "CREATE SCHEMA IF NOT EXISTS \"%s\"" flow-instance))
      (with-db-schema [conn ds] flow-instance
                      (ragtime/migrate (assoc schema-migrations :datastore (ragtime-jdbc/sql-database conn))))))
  system)

(defn rollback [{:keys [db flow-config base-migrations] :as system}]
  (let [ds (select-keys (:spec db) [:datasource])]
    (ragtime/rollback (assoc base-migrations :datastore (ragtime-jdbc/sql-database ds)))
    (doseq [flow-instance (keys @flow-config)]
    ;(doseq [flow-instance ["akvoflowsandbox"]]
      (prn (format "Processing: %s" flow-instance))
      (jdbc/execute! ds (format "DROP SCHEMA IF EXISTS \"%s\" CASCADE" flow-instance))))
  system)
