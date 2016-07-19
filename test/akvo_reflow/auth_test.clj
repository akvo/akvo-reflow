(ns akvo-reflow.auth-test
  (:require [akvo-reflow.auth :refer [wrap-auth-required wrap-basic-auth]]
            [akvo-reflow.utils :refer [wrap-config]]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(def config {:config (atom {"akvoflowsandbox" {:apiKey "123"}})})

(def basic-auth "Basic YWt2b2Zsb3dzYW5kYm94OjEyMw==")

(deftest test-wrap-functions
  (testing "wrap-config"
    (let [handler (fn [req]
                    (is (not-empty (:config req)))
                    (is (= "123" (get-in req [:config "akvoflowsandbox" :apiKey]))))
          handler (wrap-config handler config)
          req (mock/request :get "/")]
      (handler req)))

  (testing "wrap-auth-required"
    (let [handler (fn [req]
                    {:status 200
                     :body "OK"})
          handler (-> handler
                      (wrap-auth-required)
                      (wrap-basic-auth)
                      (wrap-config config))
          not-authenticated-req (mock/request :get "/")
          authenticated-req (-> not-authenticated-req
                                (mock/header "Authorization" basic-auth))]
      (is (= 401 (:status (handler not-authenticated-req))))
      (is (= 200 (:status (handler authenticated-req)))))))
