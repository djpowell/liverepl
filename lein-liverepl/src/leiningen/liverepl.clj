(ns leiningen.liverepl
  (:require
    [leiningen.core.eval]
    [leiningen.core.classpath :as cp]
    [clojure.pprint]
    [clojure.java.io :as io]))

(def ^:private use-clojure-version "1.5.1")

(def ^:private use-liverepl-version "1.2.0-SNAPSHOT")

(def ^:private tools-path (.getCanonicalPath (io/file (System/getProperty "java.home") "/../lib/tools.jar")))

(defn- find-file
  [files filename version]
  (let [results (filter #(= (.getName %) (str filename version ".jar")) files)]
    (when-not (= 1 (count results))
      (throw (ex-info "Found conflicting dependencies" {:files files})))
    (.getCanonicalPath (first results))))

(defn ^:no-project-needed liverepl
  "Connect a repl to any running java or clojure process.

liverepl allows you to connect a repl to a running Java or Clojure
process, without requiring the process to have been prepared for this
in advance.

  lein liverepl
    Display a list of available process ids to connect to.

  lein liverepl <pid> 0
    Connect to the specified process id using the system class-loader.

  lein liverepl <pid>
    Display a list of available class-loaders in a specified process

  lein liverepl <pid> <class-loader-id>
    Connect to the specified process id using the specified class-loader.

You should run this command using the same user and jvm as the target
process.
"
  [project & args]
  (let [use-project `{:dependencies [
                                     [net.djpowell/liverepl-agent ~use-liverepl-version]
                                     [net.djpowell/liverepl-server ~use-liverepl-version]
                                     [net.djpowell/liverepl-client ~use-liverepl-version]
                                     [org.clojure/clojure ~use-clojure-version]
                                     ]
                      :resource-paths [~tools-path]}
        dependency-list (cp/resolve-dependencies :dependencies use-project)
        agent-path (find-file dependency-list "liverepl-agent-" use-liverepl-version)
        server-path (find-file dependency-list "liverepl-server-" use-liverepl-version)
        client-path (find-file dependency-list "liverepl-client-" use-liverepl-version)
        clojure-path (find-file dependency-list "clojure-" use-clojure-version)]        
    (leiningen.core.eval/eval-in-project
     use-project
     `(do
        (net.djpowell.liverepl.client.Main/main (into-array String [~clojure-path ~agent-path ~server-path ~@args]))))))

