(ns akvo-reflow.endpoint.gae-test
  (:require [akvo-reflow.endpoint.fixtures :refer [system-fixture test-system]]
            [akvo-reflow.endpoint.gae :as gae]
            [akvo-reflow.utils :refer [with-db-schema]]
            [clojure.test :refer :all]
            [hugsql.core :as hugsql]
            [ring.mock.request :as mock]))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")

(use-fixtures :once system-fixture)

(deftest ^:functional gae
  (let [handler (gae/endpoint test-system)
        some-json "{\"foo\":\"bar\"}"
        ds (select-keys (-> test-system :db :spec) [:datasource])]

    (testing "post json"
      (is (= (select-keys
              (handler (mock/request :post "/gae/" (.getBytes some-json "UTF-8")))
              [:status :headers])
             {:status 200
              :headers {"Content-Type" "text/html; charset=utf-8"}})))

    (testing "verify data"
      (with-db-schema [conn ds] "akvoflowsandbox"
        (is (= (:payload (first (all-events conn))) some-json))))))
