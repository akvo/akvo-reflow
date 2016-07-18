(ns akvo-reflow.endpoint.gae-test
  (:require [akvo-reflow.endpoint.fixtures :refer [system-fixture test-system]]
            [akvo-reflow.endpoint.gae :as gae]
            [akvo-reflow.utils :refer [with-db-schema]]
            [clojure.test :refer :all]
            [hugsql.core :as hugsql]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")

(use-fixtures :once system-fixture)

(deftest ^:functional gae
  (let [handler (-> (gae/endpoint test-system)
                    (wrap-json-body {:keywords? false})
                    (wrap-json-response))
        some-json (json/generate-string {:orgId "akvoflowsandbox"
                                         :events [{:id 1 :properties [{:name "prop1"}]}]})
        ds (select-keys (-> test-system :db :spec) [:datasource])]

    (testing "post json"
      (let [req (-> (mock/request :post "/gae/" some-json)
                    (mock/content-type "application/json"))
            resp (handler req)]
        (is (= (select-keys resp [:status :headers])
               {:status 200
                :headers {"Content-Type" "application/json; charset=utf-8"}}))))

    (testing "verify data"
      (with-db-schema [conn ds] "akvoflowsandbox"
        (let [evts (all-events conn)
              first-event (:payload (first evts))]
          (is (= 1 (get first-event "id")))
          (is (= [{"name" "prop1"}] (get first-event "properties"))))))))
