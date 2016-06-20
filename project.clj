(defproject akvo-reflow "0.1.0-SNAPSHOT"
  :description "GAE-FLOW transformation service"
  :url "https://github.com/akvo/akvo-reflow"
  :min-lein-version "2.0.0"
  :dependencies [[org.akvo/commons "0.4.4-SNAPSHOT"]
                 [org.clojure/clojure "1.8.0"]
                 [com.layerware/hugsql "0.4.7"]
                 [com.stuartsierra/component "0.3.1"]
                 [cheshire "5.5.0"]
                 [compojure "1.5.0"]
                 [duct "0.6.1"]
                 [environ "1.0.3"]
                 [meta-merge "0.1.1"]
                 [ragtime "0.5.3"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.2.0"]
                 [ring/ring-mock "0.3.0"]
                 [ring-jetty-component "0.3.1"]
                 [duct/hikaricp-component "0.1.0"]
                 [org.postgresql/postgresql "9.4.1208"]]
  :plugins [[lein-environ "1.0.3"]]
  :main ^:skip-aot akvo-reflow.main
  :target-path "target/%s/"
  :aliases {"run-task" ["with-profile" "+repl" "run" "-m"]
            "setup"    ["run-task" "dev.tasks/setup"]}
  :profiles
  {:dev  [:project/dev  :profiles/dev]
   :test [:project/test :profiles/test]
   :uberjar {:aot :all}
   :profiles/dev  {}
   :profiles/test {:env {:database-url "jdbc:postgresql://localhost/test_reflow"}}
   :project/dev   {:dependencies [[duct/generate "0.6.1"]
                                  [reloaded.repl "0.2.1"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [eftest "0.1.1"]
                                  [kerodon "0.7.0"]]
                   :source-paths ["dev"]
                   :resource-paths ["test/resources"]
                   :repl-options {:init-ns user}
                   :env {:port "3000"}
                   :plugins [[com.jakemccrary/lein-test-refresh "0.15.0"]]}
   :project/test  {}})
