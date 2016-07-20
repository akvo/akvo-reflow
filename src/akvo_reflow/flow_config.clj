(ns akvo-reflow.flow-config
  (:require [akvo.commons.config :refer [get-config]]
            [clojure.java.shell :as shell]
            [com.stuartsierra.component :as component]
            [me.raynes.fs :refer [find-files exists?]]))

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
  [{:keys [flow-server-config]}]
  {:pre [(not-empty flow-server-config)
         (exists? flow-server-config)]}
  (->> #"appengine-web.xml"
       (find-files flow-server-config)
       (map get-config)
       (index-by :app-id)))

(defn reload-config
  [flow-config {:keys [flow-server-config] :as system-config}]
  (let [pull (shell/with-sh-dir flow-server-config
               (shell/sh "git" "pull"))]
    (if (zero? (:exit pull))
      (reset! flow-config (get-flow-config system-config))
      (prn (:err pull)))))
