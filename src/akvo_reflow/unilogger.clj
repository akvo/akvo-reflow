(ns akvo-reflow.unilogger
  (:require
    [akvo-reflow.event-parser :refer
     [drop-deprecated-props event-properties kind parse transform-event]]
    [cheshire.core :refer [generate-string]]
    [clj-http.client :refer [post]]
    [hugsql.core]))

(hugsql.core/def-db-fns "akvo_reflow/unilogger.sql")

; let's fake the unilog
(def unilog-url "http://localhost:3000/unilog")

(defn post-event
  ""
  [data]
  (post unilog-url {:body data}))

(defn get-unprocessed-events
  "Get all unprocessed events, transform them and return a list of maps including the transformed
   event and its ID"
  [db-uri]
  (for [data (unprocessed-events db-uri {:limit 5})         ; TODO: make limit configurable
        :let [id (:id data)
              event (:payload data)
              event-properties (event-properties event)
              kind (kind event)
              transform (transform-event kind (drop-deprecated-props kind event-properties))]]
    {:id id :transform transform}))

(defn process-events
  "Post events to the unilog"
  [db-uri schema-name]
  (let [events (get-unprocessed-events db-uri)
        payload (generate-string {:orgId schema-name :events events})
        response (post-event (generate-string payload))]
    (if (= 200 (:status response))
      (do
        (set-events-processed db-uri  {:ids (map #(:id %) events)})))))

