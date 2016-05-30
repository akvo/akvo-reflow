(ns akvo-reflow.endpoint.fixtures
  (:require
    [akvo-reflow.config :as config]
    [akvo-reflow.migrate :as migrate]
    [user :refer [dev]]
    [meta-merge.core :refer [meta-merge]]
    [reloaded.repl :refer [system init start stop go reset]]
    [ring.middleware.stacktrace :refer [wrap-stacktrace]]))


(def dev-config
  {:app {:middleware [wrap-stacktrace]}})

(def config
  (meta-merge config/defaults
              config/environ
              dev-config))


(defn system-fixture
  "Starts the system and migrates, no setup or tear down."
  [f]
  (let [conn {:connection-uri (-> config :db :uri)}]
    (try
      (migrate/migrate conn)
      (f)
      ;(migrate/rollback conn)
      )))
