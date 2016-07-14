(ns akvo-reflow.system
  (:require [akvo.commons.psql-util]
            [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.component.hikaricp :refer [hikaricp]]
            [duct.component.ragtime :refer [ragtime]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [akvo-reflow.config :refer [get-flow-config]]
            [akvo-reflow.endpoint
             [unilog :as unilog]
             [gae :as gae]]))

(def base-config
  {:app {:middleware [[wrap-not-found :not-found]
                      [wrap-defaults :defaults]]
         :not-found  "Resource Not Found"
         :defaults   (meta-merge api-defaults {})}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :app  (handler-component (:app config))
         :http (jetty-server (:http config))
         :db   (hikaricp (:db config))
         :ragtime (ragtime {:resource-path "migrations"})
         :unilog (endpoint-component unilog/endpoint)
         :gae (endpoint-component gae/endpoint)
         :flow-config (get-flow-config (:flow-server-config config)))
        (component/system-using
         {:http [:app]
          :app  [:ragtime :unilog :gae]
          :unilog [:db]
          :gae [:db]
          :ragtime [:db]
          :db [:flow-config]}))))
