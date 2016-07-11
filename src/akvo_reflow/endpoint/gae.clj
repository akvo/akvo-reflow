(ns akvo-reflow.endpoint.gae
  (:require [akvo-reflow.utils :refer [with-db-schema]]
            [cheshire.core :as json]
            [clojure.java.jdbc :as jdbc]
            [compojure.core :refer :all]
            [hugsql.core :as hugsql]
            [meta-merge.core :refer [meta-merge]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")


(defn endpoint [{:keys [db]}]
  (context "/gae" []
    (POST "/" []
      (fn [{:keys [:body]}]
        (let [body (json/generate-string (slurp body))
              ds (select-keys (-> db :spec) [:datasource])]
          (with-db-schema [conn ds] "akvoflow-1" ;; FIXME extract instance id from the payload
            (insert-event conn {:payload body})))))))
