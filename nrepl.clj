(use '[clojure.tools.nrepl.server :only (start-server stop-server)])
(defn nrepl-handler []
  (require 'cider.nrepl)
  (ns-resolve 'cider.nrepl 'cider-nrepl-handler))
(defonce server (start-server :port 7889
                              :handler (nrepl-handler)))
