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
        (if-let [org-id (get body "orgId")]
          (let [ds (select-keys (-> db :spec) [:datasource])]
            (with-db-schema [conn ds] org-id
              (doseq [event (get body "events")]
                (insert-event conn {:payload event})))
            {:status 200
             :body {:status "OK"}})
          {:status 400
           :body "orgId is required"})))))
