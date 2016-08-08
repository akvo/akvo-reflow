(ns akvo-reflow.endpoint.import-instance-test
  (:require [akvo-reflow.endpoint.fixtures :refer [system-fixture test-system]]
            [akvo-reflow.endpoint.import-instance :as import-instance]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(use-fixtures :once system-fixture)

(def instances
  {"akvoflowsandbox" {:status 200
                      :headers {"Content-Type" "text/plain"}}
   "nonexistinginstance" {:status 404
                             :headers {"Content-Type" "text/plain"}}})

(deftest ^:functional import-instance
  (let [handler (import-instance/endpoint test-system)
        instance-id "akvoflowsandbox"]

    (doseq [instance (keys instances)]
      (testing (str "import " instance)

        (let [req (mock/request :post (str "/import-instance/" instance) "")
              resp (handler req)]
          (is (= (select-keys resp [:status :headers])
                 (get instances instance))))))))
