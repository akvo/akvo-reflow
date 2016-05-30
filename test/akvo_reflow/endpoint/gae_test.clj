(ns akvo-reflow.endpoint.gae-test
  (:require [clojure.test :refer :all]
            [akvo-reflow.endpoint.gae :as gae]))

(def handler
  (gae/endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
