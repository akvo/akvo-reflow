(ns akvo-reflow.endpoint.gae
  (:require [akvo-reflow.config :as config]
            [cheshire.core :as json]
            [compojure.core :refer :all]
            [hugsql.core]
            [meta-merge.core :refer [meta-merge]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(hugsql.core/def-db-fns "akvo_reflow/endpoint/gae.sql")


(defn endpoint [{{db_uri :uri} :db :as args}]
  (println "args" args)
  (context "/gae" []

    (GET "/" [] "Hello World")

    (POST "/" []
      (fn [{:keys [:body ] :as request}]
        (println "request" request "db_uri" db_uri)
        (let [body (json/generate-string (slurp body))]
          (insert-event db_uri {:payload body}))))))
