(ns server.main
  (:import (java.net ServerSocket)
           (java.io InputStreamReader BufferedReader)))

(defn print-all [reader]
  (loop [done false]
    (let [next-line (.readLine reader)]
      (if next-line
        (do
          (println next-line)
          (recur true))
        (do
          (recur false))))))

(defn -main [& args]
  (let [s (ServerSocket. 3000)]
    (println "server starting...")
    (loop [socket (.accept s)]
     (let [reader (-> socket .getInputStream InputStreamReader. BufferedReader.)]

 ;      (println (print-all reader))
;       (.close reader)
       (.close socket)
 (println "hi")
       (recur (.accept s))))))
