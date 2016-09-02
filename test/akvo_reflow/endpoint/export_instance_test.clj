(ns akvo-reflow.endpoint.export-instance-test
  (:require [clojure.test :refer :all]
            [akvo-reflow.endpoint.export-instance :as export-instance]))

(def handler
  (export-instance/endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))
