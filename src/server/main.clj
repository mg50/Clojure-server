(ns server.main
  (:use [server.agents :only [generate-agents send-socket-to-http-agents]]
        [server.request.router :only [defrouter GET POST ANY*]]
        [server.response.actions :only [echo serve-file write-file]]
        [server.core :only [url-decode]])
  (:import (java.net ServerSocket)))



(defrouter echo-router [request params]
  (GET ".*"
       (echo (str
              "You just requested: "
              (url-decode (get-in request [:request-line :request-uri]))
              " Good job!"))))

(defrouter serve-file-router [request params]
  (GET "/.*"
       (serve-file (get-in request [:request-line :request-uri]))))

(defrouter file-write-router [request params]
  (POST "/store_data"
        (let [filename (:filename params)
              data (:data params)]
          (write-file filename data)))
  (ANY* ".*"
        [403 "Forbidden"]))


(defn -main [& args]
  (let [server (ServerSocket. 3000)]
    (println "server starting")
    (loop []
      (let [client (.accept server)
            server-type (first args)
            agents (generate-agents (cond
                                     (= server-type "serve-file") serve-file-router
                                     (= server-type "file-write") file-write-router
                                     :else echo-router))]
        (.setSoTimeout client 1000)
        (send-socket-to-http-agents agents client)
        (recur)))))
