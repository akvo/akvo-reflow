(ns akvo-reflow.endpoint.fixtures
  (:require [akvo-reflow.endpoint [status :as status]]
            [akvo-reflow.migrate :refer [migrate-base migrate-schema rollback]]
            [akvo-reflow.flow-config :refer [get-flow-config]]
            [com.stuartsierra.component :as component]
            [dev :refer [test-db-uri]]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.hikaricp :refer [hikaricp]]
            [duct.component.ragtime :refer [ragtime]]))

(defonce test-system nil)

(defn new-test-system
  []
  (-> (component/system-map
       :db (hikaricp {:uri (:connection-uri test-db-uri)})
       :base-migrations (ragtime {:resource-path "migrations/base"})
       :schema-migrations (ragtime {:resource-path "migrations/schema"})
       :flow-config (atom (get-flow-config {:flow-server-config "test/resources/flow"}))
       :status (endpoint-component status/endpoint))
      ; local path for testing fetch-and-store-entities
      ;:flow-config (atom (get-flow-config {:flow-server-config "/Users/gabriel/git/akvo-flow-server-config"})))
      (component/system-using
       {:base-migrations [:db]
        :schema-migrations [:db]
        :db [:flow-config]
        :status[:db :flow-config]})))

(defn system-fixture
  [f]
  (alter-var-root #'test-system (constantly (new-test-system)))
  (alter-var-root #'test-system component/start)
  (migrate-base test-system)
  (migrate-schema test-system)
  (f)
  (rollback test-system)
  (alter-var-root #'test-system component/stop))
