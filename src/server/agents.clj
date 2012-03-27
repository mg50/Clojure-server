(ns server.agents
  (:use [server.request.parse :only [parse-request]]
        [server.logger :only [send-request-to-logger-agent]])
  (:use (server [socket-util :only [send-message-to-socket]]
                core)
        [server.response.stringify :only [make-response-string]]))


(defn generate-agents [router]
  (doall
   (for [i (range (Integer/parseInt (config "numagents")))]
     (let [a (agent router)]
       (set-error-mode! a :continue)
       a))))


(defn request-response-cycle [agent-dispatch-fn socket]
  (let [parsed-request (parse-request (.getInputStream socket))
        [status-code resp-body] (agent-dispatch-fn parsed-request)]
    ;(send-request-to-logger-agent parsed-request)
    (send-message-to-socket socket
                           (make-response-string status-code resp-body)))
  (.close socket)
  agent-dispatch-fn)

(defn send-socket-to-http-agents [agents socket]
  (let [random-agent (rand-nth agents)]
    (send-off random-agent request-response-cycle socket)))
