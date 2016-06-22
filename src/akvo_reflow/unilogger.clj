(ns akvo-reflow.unilogger
  (:require
    [akvo-reflow.event-parser :refer
     [drop-deprecated-props event-properties kind parse transform-event]]
    [dev :refer [db-uri]]
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

(defn process-events
  []
  (doseq [data (unprocessed-events db-uri)]
    (let [event (parse (:payload data))
          event-properties (event-properties event)
          kind (kind event)
          transform (transform-event kind (drop-deprecated-props kind event-properties))
          response (post-event (generate-string transform))]
      (if (= 200 (:status response))
        (set-event-processed db-uri  {:id (:id data)})))))
