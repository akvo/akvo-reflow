(ns akvo-reflow.import
  (:require [akvo.commons.gae :as gae]
            [akvo.commons.gae.query :as q]
            [hugsql.core :as hugsql])
  (:import [org.postgresql.util PGobject PSQLException]
           com.fasterxml.jackson.databind.ObjectMapper
           java.util.Date))

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
    (let [created-datetime (cast Date (.getProperty e "createdDateTime"))
          created-at (if (nil? created-datetime) 0 (.getTime created-datetime))]
      (try
        (insert-entity db-spec {:table-name table-name
                                :created-at created-at
                                :payload (entity->jsonb e)})
        (catch PSQLException e
          (.printStackTrace e))))))

(defn datastore-spec [config]
  (assoc (select-keys config [:service-account-id :private-key-file])
         :hostname (str (:org-id config) ".appspot.com")
         :port 443))

(defn first-event-created-datetime
  [ds]
  (let [q-first-event (.prepare ds (q/query {:kind "EventQueue"
                                             :sort-by "createdDateTime"}))
        first-event (first (.asList q-first-event (q/fetch-options {:limit 1})))]
    (.getProperty first-event "createdDateTime")))

(defn fetch-and-store-entities
  [db-spec config]
  (gae/with-datastore [ds (datastore-spec config)]
    (let [t0 (first-event-created-datetime ds)]
      (doseq [kind (keys kinds)]
        (let [q-entities (.prepare ds (q/query {:kind kind
                                                :filter (q/< "createdDateTime" t0)
                                                :sort-by "createdDateTime"}))
              fetch-opts (q/fetch-options {:limit batch-size})
              result (.asQueryResultList q-entities fetch-opts)
              schema-name (:org-id config)
              table (get kinds kind)
              table-name (format "%s.%s" schema-name table)
              index-name (format "%s_payload_unique" table)]

          (new-schema db-spec {:schema-name schema-name})
          (new-table db-spec {:table-name table-name})
          (new-index db-spec {:index-name index-name :table-name table-name})

          (loop [query-result result]
            (when-not (empty? query-result)
              (insert-entities db-spec table-name query-result)
              (let [cursor (.getCursor query-result)
                    fetch-opts (q/fetch-options {:limit batch-size
                                                 :start-cursor cursor})
                    next-batch (.asQueryResultList q-entities fetch-opts)]
                (recur next-batch)))))))))
