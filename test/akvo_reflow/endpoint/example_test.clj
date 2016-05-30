(ns akvo-reflow.endpoint.example-test
  (:require [com.stuartsierra.component :as component]
            [clojure.test :refer :all]
            [kerodon.core :refer :all]
            [kerodon.test :refer :all]
            [akvo-reflow.endpoint.example :as example]))

(def handler
  (example/endpoint {}))

(deftest smoke-test
  (testing "example page exists"
    (-> (session handler)
        (visit "/example")
        (has (status? 200) "page exists"))))
