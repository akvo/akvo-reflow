(ns akvo-reflow.endpoint.gae-test
  (:require [akvo-reflow.config :as config]
            [akvo-reflow.endpoint.gae :as gae]
            [akvo-reflow.endpoint.fixtures :refer [system-fixture]]
            [clojure.test :refer :all]
            [hugsql.core :as hugsql]
            [meta-merge.core :refer [meta-merge]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.mock.request :as mock]
            ))

(hugsql/def-db-fns "akvo_reflow/endpoint/gae.sql")

(def dev-config
  {:app {:middleware [wrap-stacktrace]}})

(def config
  (meta-merge config/defaults
              config/environ
              dev-config))

(def handler
  (gae/endpoint {}))

(use-fixtures :once system-fixture)

(deftest gae-post
    (is (= (select-keys
             (handler (mock/request :post "/gae/" (.getBytes "{\"foo\":\"bar\"}" "UTF-8")))
             [:status :headers])
           {:status 200
            :headers {"Content-Type" "text/html; charset=utf-8"}})))

(deftest gae-data-in-db
  (let [conn {:connection-uri (-> config :db :uri)}]
    (is (= (all-events conn)
           "{\"foo\":\"bar\"}"))))
