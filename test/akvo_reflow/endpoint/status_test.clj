(ns akvo-reflow.endpoint.status-test
  (:require [clojure.test :refer :all]
            [akvo-reflow.endpoint.status :as status]))

(def handler
  (status/endpoint {}))

(deftest a-test
  (testing "FIXME, I don't do much..."
    (is (= 1 1))))
