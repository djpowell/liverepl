(defproject net.djpowell/liverepl-client "1.1.0"
  :description "FIXME: write description"
  :source-path "src/clj"
  :java-source-path "src/java"
  :javac-options {:debug "true"}
  :extra-classpath-dirs ~[(str (System/getProperty "java.home") "/../lib/tools.jar")]
  :main ^:skip-aot net.djpowell.liverepl.client.Main
  )
