(ns liverepl.main)

(defn- liverepl-jar-path []
  (str (System/getProperty "user.dir") "/target/liverepl.jar"))

(defn- gen-args [& args]
  (let [jar-path (liverepl-jar-path)
        jar-args [jar-path jar-path jar-path]
        full-args (reduce conj jar-args args)]
    (into-array full-args)))

(defn -main [& args]
  (let [full-args (if args (apply gen-args args) (gen-args))]
    (net.djpowell.liverepl.client.Main/main full-args)))