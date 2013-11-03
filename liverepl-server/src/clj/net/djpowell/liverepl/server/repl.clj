(ns net.djpowell.liverepl.server.repl
  (:require
   [clojure.tools.nrepl.server :as nrepls]
   [clojure.tools.nrepl.transport :as t]
   [clojure.tools.nrepl.misc :as nreplm]
   [clojure.tools.nrepl.middleware :as mw]))

(def ^:dynamic ^java.util.concurrent.Callable *do-discover* nil)

(defn list-classloaders
  "nrepl middleware for listing known classloaders in a process"
  [handler]
  (fn [{:keys [op session transport] :as msg}]
    (if (= op "liverepl-list-classloaders")
      (let [disc-resp (.call *do-discover*)]
        (t/send transport (nreplm/response-for msg
                                               :value (pr-str disc-resp)
                                               :status #{:done})))
      (handler msg))))

(mw/set-descriptor!
 #'list-classloaders
 {:requires #{"session"}
  :expects #{}
  :handles {"liverepl-list-classloaders"
            {:doc "stuff"
             :requires {}
             :optional {}
             :returns {"value" "a value"}}}})

(defn start-server
  [host port do-discover]
  (binding [*do-discover* do-discover]
    (nrepls/start-server :bind host
                         :port port
                         :handler (nrepls/default-handler #'list-classloaders))))

