(ns akvo-reflow.endpoint.fixtures
  (:require [akvo-reflow.migrate :as migrate]
            [akvo-reflow.flow-config :refer [get-flow-config]]
            [com.stuartsierra.component :as component]
            [dev :refer [test-db-uri]]
            [duct.component.hikaricp :refer [hikaricp]]
            [duct.component.ragtime :refer [ragtime]]))

(defonce test-system nil)

(defn new-test-system
  []
  (-> (component/system-map
       :db (hikaricp {:uri (:connection-uri test-db-uri)})
       :migrations (ragtime {:resource-path "migrations"})
       :flow-config (atom (get-flow-config {:flow-server-config "test/resources/flow"})))
      (component/system-using
       {:migrations [:db]
        :db [:flow-config]})))

(defn system-fixture
  [f]
  (alter-var-root #'test-system (constantly (new-test-system)))
  (alter-var-root #'test-system component/start)
  (migrate/migrate test-system)
  (f)
  (migrate/rollback test-system)
  (alter-var-root #'test-system component/stop))
