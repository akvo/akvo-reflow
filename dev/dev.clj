(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [duct.generate :as gen]
            [meta-merge.core :refer [meta-merge]]
            [reloaded.repl :refer [system init start stop go reset]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [dev.tasks :refer :all]
            [akvo-reflow.config :as config]
            [akvo-reflow.system :as system]
            [akvo-reflow.migrate :as migrate]))

(def dev-config
  {:app {:middleware [wrap-stacktrace]}})

(def config
  (meta-merge config/defaults
              config/environ
              dev-config))

(def db-uri {:connection-uri (-> config :db :uri)})

(def test-db-uri
  {:connection-uri "jdbc:postgresql://localhost/test_reflow?user=postgres"})

(defn new-system []
  (into (system/new-system config)
        {}))

(defn migrate []
  (go)
  (migrate/migrate reloaded.repl/system))

(when (io/resource "local.clj")
  (load "local"))

(gen/set-ns-prefix 'akvo-reflow)

(reloaded.repl/set-init! new-system)
