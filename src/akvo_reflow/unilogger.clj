(ns akvo-reflow.unilogger
  (:require
    [akvo-reflow.event-parser :refer
     [drop-deprecated-props event-properties kind parse transform-event]]
    [akvo-reflow.import :refer [kinds-map]]
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
  (post unilog-url {:body data}))

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
    {:id id :transform transform}))

(defn process-events
  "Post all events of a certain entity kind to the unilog"
  ([db-uri schema-name] (process-events db-uri schema-name "events"))
  ([db-uri schema-name kind]
   (if-let [events (get-unprocessed-events db-uri kind)]
     (let [payload {:orgId schema-name :events events}
           response (post-event (generate-string payload))]
       (if (= 200 (:status response))
         (set-events-processed db-uri {:ids (map #(:id %) events)})))
     (process-events db-uri schema-name kind))))

;(defn export-events [db-uri instance]
;  (doseq [kind kinds-map]
;    (process-events db-uri instance kind)))
