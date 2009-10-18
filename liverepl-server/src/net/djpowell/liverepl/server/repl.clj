(ns net.djpowell.liverepl.server.repl
  (:require clojure.main)
  (:import (java.io InputStreamReader BufferedWriter OutputStreamWriter Writer)
    (java.net ServerSocket Socket)
    (java.util.logging Logger)
    (clojure.lang LineNumberingPushbackReader)))

(def new-line (System/getProperty "line.separator"))

(defn repl-caught
  "Override the default repl-caught to not rely on PrintStream"
  [e]
  (.write #^Writer *err* (str (clojure.main/repl-exception e) new-line)))

;; The client should connect immediately after the server starts up,
;; but in case there is a problem, we will kill the serversocket,
;; terminating the server, if the client fails to connect.
(def client-connect-timeout 10000)
  
(def #^Logger trc (Logger/getLogger (str *ns*)))
  
(defn repl
  "Start a single connection repl server"
  [port backlog host]
  (let [server (ServerSocket. port backlog host)
    got-connection (atom false)
    killer-thread (Thread.
      (fn []
        (Thread/sleep client-connect-timeout)
          (when-not @got-connection
            (.fine trc "Client connect timeout: terminating server")
            (.close server)))
      "Killer Thread")
    repl-thread (Thread.
      (fn []
        (with-open [server server
          socket (.accept server)
          in (LineNumberingPushbackReader. (InputStreamReader. (.getInputStream socket)))
          out (BufferedWriter. (OutputStreamWriter. (.getOutputStream socket)))]
            (swap! got-connection (constantly true))
            (binding [*in* in
              *out* out
              *err* out]
                (try
                  (clojure.main/repl :caught repl-caught :init #(in-ns 'user))
                (catch Exception e ; discard errors caused by abrupt disconnection
                  nil)))))
      "Repl Thread")]
    (.start killer-thread)
    (.start repl-thread)))
