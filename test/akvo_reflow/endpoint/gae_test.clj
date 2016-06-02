(ns akvo-reflow.endpoint.gae-test
  (:require [akvo-reflow.config :as config]
            [akvo-reflow.endpoint.gae :as gae]
            [akvo-reflow.endpoint.fixtures :refer [system-fixture]]
            [clojure.test :refer :all]
            [dev :refer [db-uri]]
            [hugsql.core :as hugsql]
            [meta-merge.core :refer [meta-merge]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.mock.request :as mock]
            ))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")


(def handler
  (gae/endpoint {}))

(use-fixtures :once system-fixture)

(deftest gae-post [db-uri]
    (is (= (select-keys
             (handler (mock/request :post "/gae/" (.getBytes "{\"foo\":\"bar\"}" "UTF-8")))
             [:status :headers])
           {:status 200
            :headers {"Content-Type" "text/html; charset=utf-8"}})))

(deftest gae-data-in-db [db-uri]
  (println "gae" (all-events db-uri))
    (is (= (all-events db-uri)
           "{\"foo\":\"bar\"}")))
