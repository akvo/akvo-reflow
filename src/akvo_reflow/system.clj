(ns akvo-reflow.system
  (:require [akvo.commons.psql-util]
            [akvo-reflow.endpoint
             [gae :as gae]
             [import-instance :as import-instance]
             [export-instance :as export-instance]
             [reload :as reload]
             [status :as status]
             [unilog :as unilog]]
            [akvo-reflow.flow-config :refer [get-flow-config]]
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
         :base-migrations (ragtime {:resource-path "migrations/base"})
         :schema-migrations (ragtime {:resource-path "migrations/schema"})
         :flow-config (atom (get-flow-config config))
         :gae (endpoint-component gae/endpoint)
         :import-instance (endpoint-component import-instance/endpoint)
         :export-instance (endpoint-component export-instance/endpoint)
         :reload (endpoint-component reload/endpoint)
         :status (endpoint-component status/endpoint)
         :unilog (endpoint-component unilog/endpoint))
        (component/system-using
         {:http [:app]
          :app  [:flow-config :base-migrations :schema-migrations :gae :import-instance :export-instance :reload
                 :status :unilog]
          :gae [:db]
          :unilog [:db]
          :import-instance [:db :base-migrations :schema-migrations :flow-config]
          :export-instance [:db :base-migrations :schema-migrations :flow-config]
          :base-migrations [:db]
          :schema-migrations [:db]
          :reload [:db :base-migrations :schema-migrations :flow-config]
          :status[:db :base-migrations :schema-migrations :flow-config]
          :db [:flow-config]}))))
