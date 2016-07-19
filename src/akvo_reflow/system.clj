(ns akvo-reflow.system
  (:require [akvo.commons.psql-util]
            [akvo-reflow.endpoint
             [unilog :as unilog]
             [gae :as gae]
             [reload :as reload]]
            [akvo-reflow.component.flow-config :refer [flow-config]]
            [akvo-reflow.utils :refer [wrap-config]]
            [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.component.hikaricp :refer [hikaricp]]
            [duct.component.ragtime :refer [ragtime]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(def base-config
  {:app {:middleware [[wrap-config :flow-config]
                      wrap-json-response
                      [wrap-json-body :json-body]
                      [wrap-not-found :not-found]
                      [wrap-defaults :defaults]]
         :not-found  "Resource Not Found"
         :defaults   (meta-merge api-defaults {})
         :json-body {:keywords? false}}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :config config
         :app  (handler-component (:app config))
         :http (jetty-server (:http config))
         :db   (hikaricp (:db config))
         :migrations (ragtime {:resource-path "migrations"})
         :unilog (endpoint-component unilog/endpoint)
         :gae (endpoint-component gae/endpoint)
         :flow-config (flow-config config)
         :reload (endpoint-component reload/endpoint))
        (component/system-using
         {:http [:app]
          :app  [:flow-config :migrations :unilog :gae :reload]
          :unilog [:db]
          :gae [:db]
          :migrations [:db]
          :reload [:db :migrations :flow-config]
          :db [:flow-config]}))))
