(ns akvo-reflow.unilogger-test
  (:require
    [akvo-reflow.endpoint.fixtures :refer [system-fixture]]
    [akvo-reflow.unilogger :refer [process-events post-event]]
    [akvo-reflow.utils :refer [get-json-sample]]
    [clojure.test :refer :all]
    [dev :refer [test-db-uri]]
    [hugsql.core :as hugsql]))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")

(use-fixtures :once system-fixture)

(def event-samples
  ["survey_group_1.json" "survey_group_2.json"])

(deftest unilogger
  []
  ; create some unprocessed events
  (doseq [sample event-samples]
    (insert-event
      test-db-uri
      {:payload (slurp (get-json-sample sample))}))
  (testing "mark as processed after successful post"
    (with-redefs [post-event (fn [data] {:status 200})] (process-events test-db-uri))
    (is
      (=
        (count event-samples )
        (count (processed-events  test-db-uri))))))
