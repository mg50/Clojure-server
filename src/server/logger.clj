(ns server.logger
  (:use [clojure.java.io :only [writer]]
        server.core)
  (:import java.io.FileInputStream))

(defn log-request
  ([request stream]
     (with-open [wrtr (writer stream :append true)]
       (.write wrtr (str request "\n\n"))))
  ([request]
     (let [logfile-name (config "logfile")]
       (log-request request (FileInputStream. logfile-name)))))

(def logger-agent (agent nil))

(defn send-request-to-logger-agent [req]
  (send-off logger-agent log-request req))
