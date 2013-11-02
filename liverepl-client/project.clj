(defproject net.djpowell/liverepl-client "1.2.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [reply "0.2.1"]]
  :plugins [[lein-jdk-tools "0.1.1"]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :javac-options {:debug "true"}
  :main ^:skip-aot net.djpowell.liverepl.client.Main
  )
