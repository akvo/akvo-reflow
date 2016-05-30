(ns akvo-reflow.endpoint.gae
  (:require [akvo-reflow.config :as config]
            [cheshire.core :as json]
            [compojure.core :refer :all]
            [hugsql.core]
            [meta-merge.core :refer [meta-merge]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))


(hugsql.core/def-db-fns "akvo_reflow/endpoint/gae.sql")


(def dev-config
  {:app {:middleware [wrap-stacktrace]}})

(def config
  (meta-merge config/defaults
              config/environ
              dev-config))

(defn endpoint [{{db :spec} :db}]
  (context "/gae" []

    (GET "/" [] "Hello World")

    (POST "/" []
      (fn [{:keys [:body ] :as request}]
        (let [body (json/generate-string (slurp body))]
          (insert-event {:connection-uri (-> config :db :uri)} {:payload body}))))))
