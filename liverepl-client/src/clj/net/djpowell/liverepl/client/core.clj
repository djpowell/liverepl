(ns net.djpowell.liverepl.client.core
  (:require
   [clojure.tools.nrepl :as repl]
   [reply.main :as reply]
   ))
  
;; TODO ipv6 only support?

(defn connect-repl
  [host port]
  (reply/launch-nrepl {:attach (str host ":" port)
                       :input System/in
                       :output System/out})
  ;; TODO when reply disconnects, send a command to the server to shut
  ;; it down
  )

(defn run-command
  [host port cmd]
  (with-open [conn (repl/connect :host host :port port)]
    (-> (repl/client conn 1000)
        (repl/message {:op cmd})
        repl/response-values
        first)))
 