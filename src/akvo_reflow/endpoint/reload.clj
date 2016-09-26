(ns akvo-reflow.endpoint.reload
  (:require [akvo-reflow.flow-config :refer [reload-config]]
            [akvo-reflow.migrate :refer [migrate-base migrate-schema]]
            [compojure.core :refer [context POST]]))

(defn endpoint [{:keys [flow-config config] :as system}]
  (context "/reload" []
    (POST "/" []
      (let [cfg (reload-config flow-config config)]
        (try
          (migrate-base system)
          (migrate-schema system)
          {:status 302
           :headers {"Location" "/status"}}
          (catch Exception e
            {:status 500
             :body (.printStackTrace e)}))))))
