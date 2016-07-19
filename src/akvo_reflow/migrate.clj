(ns akvo-reflow.migrate
  "Migrates the tenant manager and it's tenants."
  (:require [akvo-reflow.utils :refer [with-db-schema]]
            [clojure.java.jdbc :as jdbc]
            [duct.component.ragtime :as ragtime]
            [ragtime.jdbc :as ragtime-jdbc]))

(defn migrate [{:keys [db migrations flow-config] :as system}]
  (let [ds (select-keys (:spec db) [:datasource])]
    (doseq [flow-instance (keys @(:config flow-config))]
      (prn (format "Processing: %s" flow-instance))
      (jdbc/execute! ds (format "CREATE SCHEMA IF NOT EXISTS \"%s\"" flow-instance))
      (with-db-schema [conn ds] flow-instance
        (ragtime/migrate (assoc migrations :datastore (ragtime-jdbc/sql-database conn))))))
  system)

(defn rollback [{:keys [db flow-config] :as system}]
  (let [ds (select-keys (:spec db) [:datasource])]
    (doseq [flow-instance (keys @(:config flow-config))]
      (prn (format "Processing: %s" flow-instance))
      (jdbc/execute! ds (format "DROP SCHEMA IF EXISTS \"%s\" CASCADE" flow-instance))))
  system)
