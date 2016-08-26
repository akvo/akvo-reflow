(ns akvo-reflow.endpoint.import-instance
  (:require [akvo-reflow.import :refer [fetch-and-store-entities]]
            [akvo-reflow.utils :refer [with-db-schema]]
            [compojure.core :refer :all]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "akvo_reflow/endpoint/import_instance.sql")

(defn endpoint [{:keys [config flow-config db] :as system}]
  (context "/import-instance/:instance" [instance]
    (POST "/" []
      (let [ds (select-keys (-> db :spec) [:datasource])]
        (if
          (and
            (some #(= instance %) (keys @flow-config))
            (with-db-schema
              [conn ds] instance
              (= false (:import_done (instance-status ds {:instance_id instance})))))
          (do
            (future (fetch-and-store-entities ds (get @flow-config instance)))
            {:status 200
             :headers {"Content-Type" "text/plain"}
             :body (str "Started importing instance: " instance)})
          {:status 404
           :headers {"Content-Type" "text/plain"}
           :body (str "No such instance: " instance)})))))
