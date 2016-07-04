(ns akvo-reflow.endpoint.webhook
  (:require [compojure.core :refer :all]
            [reloaded.repl :refer [reset stop go]]))

(defn endpoint [config]
  (context "/webhook" []
   (GET "/" []
     (get-in config [:flow-config :path])
     ;BORK here :( (reset)
     )))
