(ns server.app
  (:use [server.core :only [config]]
        [server.agents :only [generate-agents send-socket-to-http-agents]])
  (:import java.net.ServerSocket))


(def server-agent (agent false))

(declare run-server)
(defn app-start [router & options]
  (let [options-hash (apply assoc options)]
    (if (:async options)
      (send-off server-agent run-server router)
      (run-server router))))

(defn run-server [router]
  (let [port (Integer/parseInt (config "port"))
        server (ServerSocket. port)]
    (println (str "Server starting on port " port "..."))
    (loop []
      (let [client (.accept server)
            agents (generate-agents router)]
        (.setSoTimeout client 1000)
        (send-socket-to-http-agents agents client)
        (recur)))))
