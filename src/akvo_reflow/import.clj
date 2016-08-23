(ns akvo-reflow.import
  (:require [akvo.commons.gae :as gae]
            [akvo.commons.gae.query :as q]
            [akvo-reflow.utils :refer [with-db-schema]]
            [hugsql.core :as hugsql])
  (:import
           [org.postgresql.util PGobject]
           com.fasterxml.jackson.databind.ObjectMapper))


(def object-mapper (ObjectMapper.))

(def batch-size 300)

(def kinds-map (array-map "SurveyGroup" "survey"
                      "Survey" "form"
                      "QuestionGroup" "question_group"
                      "Question" "question"
                      ;"DeviceFiles" "device_file"
                      ;"SurveyedLocale" "data_point"
                      ;"SurveyInstance" "form_instance"
                      ;"QuestionAnswerStore" "answer"
                          ))

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
  "Fetch all entities of an instance and store them locally.
    The function loops over all entity kinds, fetches them in batches and stores them in the schema with the same name
    as the instance.
    The function can handle interruptions due to e.g. connectivity problems."
  [db-spec config]
  (gae/with-datastore [ds (datastore-spec config)]
    (let [t0 (or (first-event-created-datetime ds)
                 (java.util.Date.))
          schema-name (:app-id config)]
      (try
        (with-db-schema [conn db-spec] schema-name
          ; figure where to start if the process was interrupted at a previous run
          (let [kind-and-cursor (get-cursor conn {:instance-id schema-name})
                kind-keys-index (zipmap (keys kinds-map) (iterate inc 0))
                ; drop kinds that have already been processed, if any
                sliced-kinds (drop (#(if (nil? %) 0 %) (kind-keys-index (:kind kind-and-cursor))) (keys kinds-map))
                cursor? ((complement nil?) (:cursor kind-and-cursor))
                ; set the batch size and possibly the initial cursor
                inital-options (cond->
                                 {:limit batch-size} cursor? (assoc :start-cursor (:cursor kind-and-cursor)))]
            (loop [kinds sliced-kinds
                   options inital-options]
              ; setup a query for the kind
              (if-not (empty? kinds)
                (let [kind (first kinds)
                      query {:kind kind
                             :filter (q/< "createdDateTime" t0)
                             :sort-by "createdDateTime"}
                      table-name (get kinds-map kind)]
                  (loop [query-result (q/result ds query options)]
                    (if-not (empty? query-result)
                      (let [iter (.iterator query-result)]
                        ; for each batch save the entities and update the kind and cursor information
                        (clojure.java.jdbc/with-db-transaction [tx conn]
                          (insert-entities tx table-name (iterator-seq iter))
                          (update-cursor tx {:instance-id schema-name
                                             :kind kind
                                             :cursor-string (.toWebSafeString (.getCursor iter))}))
                        (println "cursor hash " (.toWebSafeString (.getCursor iter)))
                        (recur (q/result ds query {:limit batch-size
                                                   :start-cursor (.getCursor iter)})))))
                  (recur (rest kinds) {:limit batch-size}))))))
        (catch Exception e
          (.printStackTrace e))))))

