(ns leiningen.liverepl
  (:require
    [leiningen.core.eval]
    [clojure.pprint]
    [clojure.java.io :as io]))

(defn conc-path
  [& args]
  (.getCanonicalPath (apply io/file args)))

;; (defn jar-path
;;   [cp]
;;   (subs (.getPath (.getJarFileURL (.openConnection(.getResource (.getContextClassLoader (Thread/currentThread)) cp)))) 1))

(def maven-home (or (System/getenv "M2_HOME") (conc-path (System/getProperty "user.home") "/.m2")))

(defn ^:no-project-needed liverepl
  "I don't do a lot."
  [project & args]
  (clojure.pprint/pprint project)
  (clojure.pprint/pprint
      `{:dependencies [
                    [net.djpowell/liverepl-agent "1.2.0-SNAPSHOT"]
                    [net.djpowell/liverepl-server "1.2.0-SNAPSHOT"]
                    [net.djpowell/liverepl-client "1.2.0-SNAPSHOT"]
                    ]
        :resource-paths [~(str (System/getProperty "java.home") "/../lib/tools.jar")]})
  (leiningen.core.eval/eval-in-project
   `{:dependencies [
                    [net.djpowell/liverepl-agent "1.2.0-SNAPSHOT"]
                    [net.djpowell/liverepl-server "1.2.0-SNAPSHOT"]
                    [net.djpowell/liverepl-client "1.2.0-SNAPSHOT"]
                    ]
     :resource-paths [~(str (System/getProperty "java.home") "/../lib/tools.jar")]}
   `(do
   ;; `(letfn [(jar-path#
   ;;            [cp#]
   ;;            (subs (.getPath (.getJarFileURL (.openConnection (.getResource (.getContextClassLoader (Thread/currentThread)) cp#)))) 1))]
      ;; (javax.swing.JOptionPane/showMessageDialog nil (jar-path# "clojure/main.class") "" javax.swing.JOptionPane/INFORMATION_MESSAGE)
      ;; (javax.swing.JOptionPane/showMessageDialog nil (jar-path# "net/djpowell/liverepl/server/repl.clj") "" javax.swing.JOptionPane/INFORMATION_MESSAGE)
      (prn "CC" (.getResource (.getContextClassLoader (Thread/currentThread)) "clojure/main.class"))
      (prn "CS" (.getResource (.getContextClassLoader (Thread/currentThread)) "net/djpowell/liverepl/server/repl.clj"))
      (let [clojure-jar# (conc-path ~maven-home "/repository/org/clojure/clojure/1.5.1/clojure-1.5.1.jar")
            agent-jar# (conc-path ~maven-home "/repository/net/djpowell/liverepl-agent/1.2.0-SNAPSHOT/liverepl-agent-1.2.0-SNAPSHOT.jar")
            server-jar# (conc-path ~maven-home "/repository/net/djpowell/liverepl-server/1.2.0-SNAPSHOT/liverepl-server-1.2.0-SNAPSHOT.jar")]
        (net.djpowell.liverepl.client.Main/main (into-array String [clojure-jar# agent-jar# server-jar# ~@args]))
        (println "Hi!")))))
