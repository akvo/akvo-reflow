(ns akvo-reflow.endpoint.gae
  (:require [akvo-reflow.utils :refer [with-db-schema]]
            [compojure.core :refer :all]
            [hugsql.core :as hugsql]
            [meta-merge.core :refer [meta-merge]]))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")

(defn endpoint [{:keys [db]}]
  (context "/gae" []
    (POST "/" []
      (fn [{:keys [:body]}]
        (let [ds (select-keys (-> db :spec) [:datasource])]
          (with-db-schema [conn ds] "akvoflowsandbox" ;; FIXME extract instance id from the payload
            (insert-event conn {:payload body})))))))
