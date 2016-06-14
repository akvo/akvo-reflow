(ns akvo-reflow.endpoint.fixtures
  (:require
    [akvo-reflow.migrate :as migrate]
    [dev :refer [test-db-uri]]
    [user :refer [dev]]
    [meta-merge.core :refer [meta-merge]]
    [reloaded.repl :refer [system init start stop go reset]]
    [ring.middleware.stacktrace :refer [wrap-stacktrace]]))


(defn system-fixture
  "Migrate creates the events table."
  [f]
  (try
    (migrate/migrate test-db-uri)
    (f)
    (migrate/rollback test-db-uri)))
