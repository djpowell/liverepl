(defproject net.djpowell/liverepl-client "1.2.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev
             {:resource-paths [~(str (System/getProperty "java.home") "/../lib/tools.jar")]}}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :javac-options {:debug "true"}
  :main ^:skip-aot net.djpowell.liverepl.client.Main
  )
