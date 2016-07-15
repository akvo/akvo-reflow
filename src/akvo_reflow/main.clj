(ns akvo-reflow.main
  (:gen-class)
  (:require [akvo-reflow.auth :refer [wrap-basic-auth wrap-auth-required]]
            [akvo-reflow.config :as config]
            [akvo-reflow.migrate :as migrate]
            [akvo-reflow.system :refer [new-system]]
            [com.stuartsierra.component :as component]
            [duct.middleware.errors :refer [wrap-hide-errors]]
            [duct.util.runtime :refer [add-shutdown-hook]]
            [meta-merge.core :refer [meta-merge]]))

(defonce ^:private system nil)

(def prod-config
  {:app {:middleware ^:prepend [wrap-auth-required
                                wrap-basic-auth
                                [wrap-hide-errors :internal-error]]
         :internal-error "Internal Server Error"}})

(def config
  (meta-merge config/defaults
              config/environ
              prod-config))

(defn- reload-config
  [component]
  (config/get-flow-config (:flow-server-config config)))

(defn- update-system
  [system]
  (component/update-system system [:flow-config] reload-config))

(defn reload-flow-config []
  (alter-var-root #'system update-system))

(defn -main [& args]
  (alter-var-root #'system (constantly (new-system config)))
  (println "Starting HTTP server on port" (-> system :http :port))
  (add-shutdown-hook ::stop-system #(component/stop system))
  (alter-var-root #'system component/start)
  (migrate/migrate system))
