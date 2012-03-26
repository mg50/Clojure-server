(ns server.agents
  (:use [server.request.parse :only [parse-request]])
  (:use (server [socket-util :only [send-message-to-socket]]
                [response :only [make-response-string]]
                core)))


(def max-threadpool-size 10)

(defn generate-agents [router]
  (doall
   (for [i (range (Integer/parseInt (config-property "maxthreads")))]
     (agent router))))


(defn request-response-cycle [agent-dispatch-fn socket]
  (let [parsed-request (parse-request (.getInputStream socket))
        [status-code resp-body] (agent-dispatch-fn parsed-request)]
    (send-message-to-socket socket
                            (make-response-string status-code resp-body)))
  (.close socket)
  agent-dispatch-fn)

(defn send-socket-to-http-agents [agents socket]
  (let [random-agent (rand-nth agents)]
    (send random-agent request-response-cycle socket)))
