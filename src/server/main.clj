(ns server.main
  (:use [server.agents :only [generate-agents send-socket-to-http-agents]]
        [server.request.router :only [defrouter GET POST ANY*]]
        [server.response.actions :only [echo serve-file write-file]]
        [server.core :only [url-decode config]])
  (:import (java.net ServerSocket))
  (:gen-class))


(defrouter integrated-router [request params]
  (GET #"/static/(.*)"
       (let [path (:$1 params)]
         (serve-file path)))
  (POST "/store_data"
        (let [filename (:filename params)
              data (:data params)]
          (write-file filename data)))
  (GET #"(.*)"
       (echo (str
              "You just requested: "
              (url-decode (:$1 params))
              " Good job!")))
  (ANY* ".*"
        [403 "Forbidden"]))

(comment
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
          [403 "Forbidden"])))


(defn -main [& args]
  (let [port (Integer/parseInt (config "port"))
        server (ServerSocket. port)]
    (println (str "Server starting on port " port "..."))
    (loop []
      (let [client (.accept server)
            server-type (first args)
            agents (generate-agents integrated-router)]
        (.setSoTimeout client 1000)
        (send-socket-to-http-agents agents client)
        (recur)))))
