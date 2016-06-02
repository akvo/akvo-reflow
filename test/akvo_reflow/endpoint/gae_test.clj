(ns akvo-reflow.endpoint.gae-test
  (:require [akvo-reflow.endpoint.gae :as gae]
            [akvo-reflow.endpoint.fixtures :refer [system-fixture]]
            [clojure.test :refer :all]
            [dev :refer [db-uri]]
            [hugsql.core :as hugsql]
            [meta-merge.core :refer [meta-merge]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.mock.request :as mock]))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")


(def handler
  (gae/endpoint {:db {:uri db-uri}}))

(use-fixtures :once system-fixture)

(deftest ^:functional gae
  []
  (let [some-json "{\"foo\":\"bar\"}"]

    (testing "post json"
      (is (= (select-keys
               (handler (mock/request :post "/gae/" (.getBytes some-json "UTF-8")))
               [:status :headers])
             {:status 200
              :headers {"Content-Type" "text/html; charset=utf-8"}})))

    (testing "verify data"
      (is (= (:payload (first (all-events db-uri)))
             some-json)))))
