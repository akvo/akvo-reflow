(ns akvo-reflow.import-test
  (:require [akvo-reflow.endpoint.fixtures :refer [system-fixture test-system]]
            [akvo-reflow.import :refer [fetch-and-store-entities]]
            [clojure.test :refer :all]))

;(use-fixtures :once system-fixture)



; This test is only a convenient wrapper to call fetch-and-store-entities and can only be run locally
;(deftest ^:functional import-entities
;         (let [db-spec (select-keys (-> test-system :db :spec) [:datasource])
;               flow-config (deref (get-in test-system [:flow-config]))]
;
;           (fetch-and-store-entities db-spec (get flow-config "akvoflowsandbox"))
;           ))



