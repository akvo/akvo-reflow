(ns akvo-reflow.endpoint.fixtures
  (:require [akvo-reflow.config :refer [get-flow-config]]
            [akvo-reflow.migrate :as migrate]
            [com.stuartsierra.component :as component]
            [dev :refer [test-db-uri]]
            [duct.component.hikaricp :refer [hikaricp]]
            [duct.component.ragtime :refer [ragtime]]))

(defonce test-system nil)

(defn new-test-system
  []
  (-> (component/system-map
       :db (hikaricp {:uri (:connection-uri test-db-uri)})
       :ragtime (ragtime {:resource-path "migrations"})
       :flow-config (get-flow-config "test/resources/flow"))
      (component/system-using
       {:ragtime [:db]
        :db [:flow-config]})))

(defn system-fixture
  [f]
  (alter-var-root #'test-system (constantly (new-test-system)))
  (alter-var-root #'test-system component/start)
  (migrate/migrate test-system)
  (f)
  (migrate/rollback test-system)
  (alter-var-root #'test-system component/stop))
