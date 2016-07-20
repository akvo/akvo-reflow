(ns akvo-reflow.endpoint.reload
  (:require [akvo-reflow.flow-config :refer [reload-config]]
            [akvo-reflow.migrate :refer [migrate]]
            [compojure.core :refer [context POST]]))

(defn endpoint [{:keys [flow-config config] :as system}]
  (context "/reload" []
   (POST "/" []
     (let [cfg (reload-config flow-config config)]
       (migrate system)
       {:status 200
        :body "OK"}))))
