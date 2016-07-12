(ns akvo-reflow.import
  (:require [akvo.commons.gae :as gae]
            [akvo.commons.gae.query :as q]
            [akvo-reflow.utils :refer [with-db-schema]]
            [hugsql.core :as hugsql])
  (:import [org.postgresql.util PGobject]
           com.fasterxml.jackson.databind.ObjectMapper))

(def object-mapper (ObjectMapper.))

(def batch-size 300)

(def kinds (array-map "SurveyGroup" "survey"
                      "Survey" "form"
                      "QuestionGroup" "question_group"
                      "Question" "question"
                      "DeviceFiles" "device_file"
                      "SurveyedLocale" "data_point"
                      "SurveyInstance" "form_instance"
                      "QuestionAnswerStore" "answer"))

(hugsql/def-db-fns "akvo_reflow/import.sql" {:quoting :ansi})

(defn entity->jsonb
  [entity]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (.writeValueAsString object-mapper entity))))

(defn insert-entities
  [db-spec table-name entities]
  (doseq [e entities]
    (let [created-datetime (.getProperty e "createdDateTime")
          created-at (if (nil? created-datetime) 0 (.getTime created-datetime))]
      (try
        (insert-entity db-spec {:table-name table-name
                                :created-at created-at
                                :payload (entity->jsonb e)})
        (catch Exception e
          (.printStackTrace e))))))

(defn datastore-spec [config]
  (assoc (select-keys config [:service-account-id :private-key-file])
         :hostname (:domain config)
         :port 443))

(defn first-event-created-datetime
  [ds]
  (let [first-event (first (q/result ds
                                     {:kind "EventQueue"
                                      :sort-by "createdDateTime"}
                                     {:limit 1}))]
    (when first-event
      (.getProperty first-event "createdDateTime"))))

(defn fetch-and-store-entities
  [db-spec config]
  (gae/with-datastore [ds (datastore-spec config)]
    (let [t0 (or (first-event-created-datetime ds)
                 (java.util.Date.))
          schema-name (:app-id config)]
      (with-db-schema [conn db-spec] schema-name
        (doseq [kind (keys kinds)]
          (let [query {:kind kind
                       :filter (q/< "createdDateTime" t0)
                       :sort-by "createdDateTime"}
                table-name (get kinds kind)]
            (loop [query-result (q/result ds query {:limit batch-size})]
              (when-not (empty? query-result)
                (let [iter (.iterator query-result)]
                  (insert-entities conn table-name (iterator-seq iter))
                  (recur (q/result ds query {:limit batch-size
                                             :start-cursor (.getCursor iter)})))))))))))

(comment

  (def sandbox {:app-id "akvoflowsandbox", :cartodb-sql-api nil, :s3bucket "akvoflowsandbox", :access-key "AKIAJ73CXGSMRTVHXFFQ", :apiKey "nk34aR11m9", :secret-key "GABRIEL", :alias "akvoflowsandbox.appspot.com", :private-key-file "/home/ivan/workspace/akvo/src/akvo-flow-server-config/akvoflowsandbox/akvoflowsandbox.p12", :domain "akvoflowsandbox.appspot.com", :service-account-id "account-1@akvoflowsandbox.iam.gserviceaccount.com", :cartodb-api-key nil})

  (def kinds {"QuestionGroup" "question_group"})

  (fetch-and-store-entities {:connection-uri "jdbc:postgresql://localhost/reflow?user=postgres"} sandbox)

  )
