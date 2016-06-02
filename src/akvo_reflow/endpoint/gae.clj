(ns akvo-reflow.endpoint.gae
  (:require [cheshire.core :as json]
            [compojure.core :refer :all]
            [hugsql.core]
            [meta-merge.core :refer [meta-merge]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(hugsql.core/def-db-fns "akvo_reflow/endpoint/gae.sql")


(defn endpoint [{{db_uri :uri} :db}]
  (context "/gae" []

    (GET "/" [] "Hello World")

    (POST "/" []
      (fn [{:keys [:body ]}]
        (let [body (json/generate-string (slurp body))]
          (insert-event db_uri {:payload body}))))))
