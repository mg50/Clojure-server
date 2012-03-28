(ns server.app
  (:use [server.core :only [config]]
        [server.ageents :only [generate-agents send-socket-to-http-agents]])
  (:import java.net.ServerSocket))

(defn run-server [router]
  (let [port (Integer/parseInt (config "port"))
        server (ServerSocket. port)]
    (println (str "Server starting on port " port "..."))
    (loop []
      (let [client (.accept server)
            agents (generate-agents integrated-router)]
        (.setSoTimeout client 1000)
        (send-socket-to-http-agents agents client)
        (recur)))))
