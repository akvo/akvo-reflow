(ns akvo-reflow.main
  (:gen-class)
  (:require [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [duct.middleware.errors :refer [wrap-hide-errors]]
            [duct.util.runtime :refer [add-shutdown-hook]]
            [meta-merge.core :refer [meta-merge]]
            [akvo-reflow.config :as config]
            [akvo-reflow.system :refer [new-system]]
            [akvo-reflow.migrate :as migrate]))

(def prod-config
  {:app {:middleware     [[wrap-hide-errors :internal-error]]
         :internal-error (io/resource "errors/500.html")}})

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
