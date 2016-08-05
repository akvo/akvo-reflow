(ns akvo-reflow.endpoint.import-test
  (:require [clojure.test :refer :all]
            [akvo-reflow.endpoint.import :as import]))

(def handler
  (import/endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))
