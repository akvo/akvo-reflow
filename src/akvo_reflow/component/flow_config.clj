(ns akvo-reflow.component.flow-config
  (:require [clojure.java.io :as io]
            [com.stuartsierra.component :as component]))

(defn read-config
  ""
  [path]
  (slurp (io/resource path)))

(defrecord FlowConfig [path]
  component/Lifecycle
  (start [this]
    (assoc this :flow-instances (read-config path)))
  (stop [this]
    (assoc this :flow-instances nil)))

(defn flow-config [path]
  (->FlowConfig path))
