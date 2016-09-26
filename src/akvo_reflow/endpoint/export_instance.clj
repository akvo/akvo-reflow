(ns akvo-reflow.endpoint.export-instance
  (:require [akvo-reflow.unilogger :refer [export-events]]
            [akvo-reflow.utils :refer [with-db-schema]]
            [compojure.core :refer :all]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "akvo_reflow/endpoint/import_instance.sql")

(defn endpoint [{:keys [flow-config db] :as system}]
  (context "/export-instance/:instance" [instance]
    (POST "/" []
      (let [ds (select-keys (-> db :spec) [:datasource])]
        (println "instance: " instance)
        (if
          (some #(= instance %) (keys @flow-config))
          (with-db-schema
            [conn ds] instance
            (let [status (instance-status ds {:instance_id instance})]
              (if
                (= false (:import_done status))
                {:status 403
                 :headers {"Content-Type" "text/plain"}
                 :body (str "Can't export instance " instance " before importing is done.")}
                (if
                  (= true (:export_done status))
                  {:status 403
                   :headers {"Content-Type" "text/plain"}
                   :body (str "Export of instance " instance " already done.")}
                  (do
                    (future (export-events ds instance))
                    {:status 200
                     :headers {"Content-Type" "text/plain"}
                     :body (str "Started exporting instance: " instance)})))))
          {:status 404
           :headers {"Content-Type" "text/plain"}
           :body (str "No such instance: " instance)})))))
