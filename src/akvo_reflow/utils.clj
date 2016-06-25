(ns akvo-reflow.utils
  (:require [clojure.java.io :as io]))

(defn get-json-sample
  ""
  [file-name]
  (io/resource (str "akvo_reflow/gae_json_samples/" file-name)))