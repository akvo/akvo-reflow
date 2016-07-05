(ns akvo-reflow.endpoint.reload
  (:require [akvo-reflow.main :as main]
            [clojure.java.shell :as shell]
            [compojure.core :refer [routes POST]]))

(defn reload-endpoint [config]
  (routes
   (POST "/" []
     (let [pull (shell/with-sh-dir (:flow-server-config main/config) ;; FIXME
                  (shell/sh "git" "pull"))]
       (when (zero? (:exit pull))
         (main/reload-flow-config))))))
