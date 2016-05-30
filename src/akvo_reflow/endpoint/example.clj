(ns akvo-reflow.endpoint.example
  (:require [compojure.core :refer :all]
            [clojure.java.io :as io]))

(defn endpoint [{{db :spec} :db}]
  (context "/example" []
    (GET "/" []
      (io/resource "akvo_reflow/endpoint/example/example.html"))))
