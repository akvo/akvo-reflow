(ns akvo-reflow.unilogger-test
  (:require [akvo-reflow.endpoint.fixtures :refer [system-fixture test-system]]
            [akvo-reflow.unilogger :refer [process-events post-event]]
            [akvo-reflow.utils :refer [get-json-sample]]
            [akvo-reflow.utils :refer [with-db-schema]]
            [clojure.test :refer :all]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")

(use-fixtures :once system-fixture)

(def event-samples
  ["survey_group_1.json" "survey_group_2.json"])

(deftest unilogger ; create some unprocessed events
  (let [ds (select-keys (-> test-system :db :spec) [:datasource])
        schema-name "akvoflowsandbox"]
    (with-db-schema [conn ds] schema-name
      (doseq [sample event-samples]
        (insert-event conn {:payload (slurp (get-json-sample sample))}))
      (testing "mark as processed after successful post"
        (with-redefs [post-event (fn [data] {:status 200})] (process-events conn schema-name))
        (is
         (=
          (count event-samples)
          (count (processed-events conn))))))))
