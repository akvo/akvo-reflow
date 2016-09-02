(ns akvo-reflow.unilogger
  (:require
    [akvo-reflow.event-parser :refer
     [drop-deprecated-props event-properties kind parse transform-event]]
    [akvo-reflow.import :refer [kinds-map]]
    [akvo-reflow.utils :refer [with-db-schema]]
    [cheshire.core :refer [generate-string]]
    [clj-http.client :refer [post]]
    [hugsql.core]))

(hugsql.core/def-db-fns "akvo_reflow/unilogger.sql")

; let's fake the unilog
(def unilog-url "http://localhost:3000/unilog")

(def unilog-limit 5)

(defn post-event
  ""
  [data]
  ; ignore exceptions, everything except status 200 is considered an error in process-events
  (post unilog-url {:body data :throw-exceptions false}))

(defn get-unprocessed-events
  "Get all unprocessed events, transform them and return a list of maps including the transformed
   event and its ID"
  [db-uri entity-type]
  (for [data (unprocessed-events db-uri {:table-name entity-type :limit unilog-limit})
        :let [id (:id data)
              event (:payload data)
              event-properties (event-properties event)
              kind (kind event)
              transform (transform-event kind (drop-deprecated-props kind event-properties))]]
    {:id id :event transform}))

(defn process-events
  "Post all events of a certain entity kind to the unilog"
  ([db-uri schema-name] (process-events db-uri schema-name "events"))
  ([db-uri schema-name kind]
   (loop [events (get-unprocessed-events db-uri kind)]
     (if-not (empty? events)
       ; TODO: should this be wrapped in a transaction?
       (do
         (let [payload {:orgId schema-name :events (map #(:event %) events)}
               response (post-event (generate-string payload))]
           (if (= 200 (:status response))
             (do
               (set-events-processed db-uri {:table-name kind :ids (map #(:id %) events)})
               (recur (get-unprocessed-events db-uri kind)))
             (do
               (set-export-interrupted db-uri {:instance-id schema-name
                                               :error-status (:status response)
                                               :error-message (:body response)})
               true))))))))

(defn export-events [db-spec instance]
  (try
    (with-db-schema [conn db-spec] instance
      (if-not
        (loop [kinds kinds-map]
          (if-not (empty? kinds)
            (if-not (process-events conn instance (-> kinds first last))
              (recur (rest kinds))
              true)))
        (set-export-done conn {:instance-id instance})))
    (catch Exception e
      (.printStackTrace e))))
