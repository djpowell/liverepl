(defproject lein-liverepl "1.2.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                  [net.djpowell/liverepl-agent "1.2.0-SNAPSHOT"]
                  [net.djpowell/liverepl-server "1.2.0-SNAPSHOT"]
                  [net.djpowell/liverepl-client "1.2.0-SNAPSHOT"]
                ]
  :resource-paths ~[(str (System/getProperty "java.home") "/../lib/tools.jar")]
  :jar-exclusions [#"\.jar$"]
  :eval-in-leiningen true)
