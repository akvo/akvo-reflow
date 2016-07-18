(ns akvo-reflow.utils
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]))

(defn get-json-sample
  ""
  [file-name]
  (io/resource (str "akvo_reflow/gae_json_samples/" file-name)))

(defn get-schema-sql
  "Returns a `SET SESSION search_path` statement,
  for a given schema-name or `DEFAULT`"
  ([]
   (get-schema-sql nil))
  ([schema-name]
   (if (empty? schema-name)
     "SET SESSION search_path TO DEFAULT"
     (format "SET SESSION search_path TO \"%s\", public" schema-name))))


(defmacro with-db-schema
  [[db-conn db-spec] schema-name & body]
  `(jdbc/with-db-connection [~db-conn ~db-spec]
     (try
       (jdbc/execute! ~db-conn (get-schema-sql ~schema-name) {:transaction? false})
       ~@body
       (finally
         (jdbc/execute! ~db-conn (get-schema-sql) {:transaction? false})))))

(defn wrap-config
  "Calls handler with an extra :config key associated
  in the request"
  [handler config]
  (fn [req]
    (handler (assoc req :config config))))
