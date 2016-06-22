(ns akvo-reflow.unilogger-test
  (:require
    [akvo-reflow.unilogger :refer [post-event process-events]]
    [clojure.test :refer :all]
    [user :refer [dev]]
    [reloaded.repl :refer [go stop]]))

(deftest unilogger
         []
         (dev)
         (go)
         (println (process-events)))