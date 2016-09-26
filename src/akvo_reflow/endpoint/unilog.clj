(ns akvo-reflow.endpoint.unilog
  (:require [compojure.core :refer :all]
            [clojure.java.io :as io]
            [cheshire.core :as json]))

; endpoint mocking the unilog
(defn endpoint [{{db :spec} :db}]
  (context "/unilog" []
    (GET "/" []
      "unilog")
    (POST "/" []
      (fn [{:keys [:body]}]
        (println "Got some JSON:")
        (println (slurp body))
        {:status 200
         :body "OK"}))))
