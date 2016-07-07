(ns akvo-reflow.config-test
  (:require
    [akvo-reflow.config :refer [get-flow-config]]
    [clojure.java.io :as io]
    [clojure.test :refer :all]
    ))

(def test-xml-path (io/resource "akvo_reflow/akvo_flow_config/"))

(deftest flow-config
  (testing "test parsing of the appengine XML"
    (is
      (= (get-flow-config test-xml-path)
         {"akvoflowsandbox"
          {:app-id "akvoflowsandbox",
           :cartodb-sql-api nil,
           :s3bucket "akvoflowsandbox",
           :access-key "AWS-IDENTIFYER",
           :apiKey "REST-PRIVATE-KEY",
           :secret-key "AWS-SECRET-KEY",
           :alias "akvoflowsandbox.appspot.com",
           :private-key-file "/Users/gabriel/git/akvo-reflow/test/resources/akvo_reflow/akvo_flow_config/akvoflowsandbox.p12",
           :domain "akvoflowsandbox.appspot.com",
           :service-account-id "account@domain.com",
           :cartodb-api-key nil}}))))