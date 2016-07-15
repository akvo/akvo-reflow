(ns akvo-reflow.auth
  (:require [buddy.auth :refer [authenticated?]]
            [buddy.auth.http :as http]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.middleware :refer [wrap-authentication]]))

(defn- authenticate
  [request authdata]
  (let [username (:username authdata)
        password (:password authdata)
        api-key (get-in request [:config username :apiKey])]
    (when (= password api-key)
      username)))

(defn wrap-basic-auth
  [handler]
  (wrap-authentication handler
                       (http-basic-backend {:realm "Akvo"
                                            :authfn authenticate})))

(defn wrap-auth-required
  [handler]
  (fn [req]
    (if (authenticated? req)
      (handler req)
      (http/response "Unauthorized" 401))))
