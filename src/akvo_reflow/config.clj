(ns akvo-reflow.config
  (:require [akvo.commons.config :refer [get-config]]
            [environ.core :refer [env]]
            [me.raynes.fs :refer [find-files exists?]])
  (:import java.io.File))

(def defaults
  ^:displace {:http {:port 3000}})

(def environ
  {:http {:port (some-> env :port Integer.)}
   :db   {:uri  (env :database-url)}
   :flow-server-config (env :flow-server-config)})

(defn- index-by
  "Helper function similar to group-by but with a
  single value instead of a vector"
  [key-fn coll]
  (reduce (fn [m val]
            (assoc m (key-fn val) val))
          {}
          coll))

(defn get-flow-config
  "Returns a map {\"app-id\" {config}}"
  [flow-server-config]
  {:pre [(not (nil? flow-server-config))
         (not-empty flow-server-config)
         (exists? flow-server-config)]}
  (->> #"appengine-web.xml"
       (find-files flow-server-config)
       (map get-config)
       (index-by :app-id)))
