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

(defn -main [& args]
  (let [system (new-system config)]
    (println "Starting HTTP server on port" (-> system :http :port))
    (add-shutdown-hook ::stop-system #(component/stop system))
    (migrate/migrate {:connection-uri (-> config :db :uri)})
    (component/start system)))