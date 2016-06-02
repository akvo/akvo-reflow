(ns akvo-reflow.endpoint.fixtures
  (:require
    ;[akvo-reflow.config :as config]
    [akvo-reflow.migrate :as migrate]
    [dev :refer [db-uri]]
    [user :refer [dev]]
    [meta-merge.core :refer [meta-merge]]
    [reloaded.repl :refer [system init start stop go reset]]
    [ring.middleware.stacktrace :refer [wrap-stacktrace]]))


(defn system-fixture
  "Starts the system and migrates, no setup or tear down."
  [f]
  (try
    (dev)
    ;(go)
    (migrate/migrate db-uri)
    (f)
    (migrate/rollback db-uri)
    ;(finally (stop))
    ))
