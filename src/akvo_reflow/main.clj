(ns akvo-reflow.main
  (:gen-class)
  (:require [akvo-reflow.config :as config]
            [akvo-reflow.system :refer [new-system]]
            [akvo-reflow.migrate :as migrate]
            [com.stuartsierra.component :as component]
            [duct.middleware.errors :refer [wrap-hide-errors]]
            [duct.util.runtime :refer [add-shutdown-hook]]
            [meta-merge.core :refer [meta-merge]]))

(def prod-config
  {:app {:middleware     [[wrap-hide-errors :internal-error]]
         :internal-error "Internal Server Error"}})

(def config
  (meta-merge config/defaults
              config/environ
              prod-config))

(defonce ^:private system nil)

(defn reload-flow-config []
  (alter-var-root #'system
                  (fn [s]
                    (component/update-system s
                                             [:flow-config]
                                             (fn [c]
                                               (config/get-flow-config (:flow-server-config config)))))))

(defn -main [& args]
  (alter-var-root #'system (constantly (new-system config)))
  (println "Starting HTTP server on port" (-> system :http :port))
  (add-shutdown-hook ::stop-system #(component/stop system))
  (alter-var-root #'system component/start)
  (migrate/migrate system))
