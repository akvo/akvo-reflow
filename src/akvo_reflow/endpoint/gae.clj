(ns akvo-reflow.endpoint.gae
  (:require [akvo-reflow.unilogger :refer [process-events]]
            [akvo-reflow.utils :refer [with-db-schema]]
            [compojure.core :refer [context POST]]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")

(defn do-process-events
  "wrapper around the future to be able swap it out for testing"
  [conn org-id]
  (future (process-events conn org-id)))

(defn endpoint [{:keys [db]}]
  (context "/gae" []
    (POST "/" []
      (fn [{:keys [:body]}]
        (if-let [org-id (get body "orgId")]
          (let [ds (select-keys (-> db :spec) [:datasource])]
            (with-db-schema [conn ds] org-id
              (doseq [event (get body "events")]
                (insert-event conn {:payload event}))
              (do-process-events conn org-id))
            {:status 200
             :body {:status "OK"}})
          {:status 400
           :body "orgId is required"})))))
