(ns akvo-reflow.endpoint.unilog-test
  (:require [com.stuartsierra.component :as component]
            [clojure.test :refer :all]
            [kerodon.core :refer :all]
            [kerodon.test :refer :all]
            [akvo-reflow.endpoint.unilog :as unilog]))

(def handler
  (unilog/endpoint {}))

(deftest smoke-test
  (testing "unilog page exists"
    (-> (session handler)
        (visit "/unilog")
        (has (status? 200) "page exists"))))
