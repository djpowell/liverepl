(defproject liverepl "1.2.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [clojure-complete "0.2.3"]
                 [cider/cider-nrepl "0.18.0-SNAPSHOT"]]
  :java-source-paths ["liverepl-agent/src" "liverepl-server/src"]
  :manifest {"Agent-Class" "net.djpowell.liverepl.agent.Agent"
             "Main-Class" "net.djpowell.liverepl.client.Main"}
  :uberjar-name "liverepl.jar"
  :main liverepl.main
  :plugins [[lein-jdk-tools "0.1.1"]])
