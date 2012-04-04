(ns server.app
  (:use [server.core :only [initialize-config config]]
        [server.agents :only [generate-agents send-socket-to-http-agents]])
  (:import java.net.ServerSocket))

(defn parse-options [opts]
  (if (empty? opts)
    {}
    (let [unparsed (apply assoc {} opts)]
      (reduce (fn [parsed [key val]]
                (condp = key
                    "-p" (assoc parsed "port" val)
                    "-d" (assoc parsed "webroot" val)
                    parsed))
              {}
              unparsed))))

(defn run-server
  ([router]
     (run-server router {}))
  ([router opts]
     (initialize-config (parse-options opts))
     (let [port (Integer/parseInt (config "port"))
           server (ServerSocket. port)]
       (println (str "Server starting on port " port "..."))
       (loop []
         (let [client (.accept server)
               agents (generate-agents router)]
           (.setSoTimeout client 1000)
           (send-socket-to-http-agents agents client)
           (recur)))))
)
