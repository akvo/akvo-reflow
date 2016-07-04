(ns akvo-reflow.system
  (:require [akvo.commons.psql-util]
            [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.component.hikaricp :refer [hikaricp]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [akvo-reflow.endpoint
             [unilog :as unilog]
             [gae :as gae]
             [webhook :as webhook]]
            [akvo-reflow.component.flow-config :refer [flow-config]]))

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
         :unilog (endpoint-component unilog/endpoint)
         :gae (endpoint-component gae/endpoint)
         :webhook (endpoint-component webhook/endpoint)
         :flow-config (flow-config (get-in config [:flow-config :path])))
        (component/system-using
         {:http [:app]
          :app  [:unilog :gae :webhook]
          :unilog [:db]
          :gae [:db :flow-config]
          :webhook [:flow-config]}))))
