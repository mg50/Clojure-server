(ns server.socket-util
  (:use [clojure.java.io :only [writer]])
  (:import (java.io BufferedReader BufferedWriter InputStreamReader OutputStreamWriter)))


(defn stream-to-string [rdr length]
  (try
    (let [bytes (repeatedly length #(.read rdr))]
      (apply str (map char bytes)))
    (catch Exception e
      nil)))

(defn send-message-to-socket [sock resp-string]
  (try
    (with-open [wrtr (writer (.getOutputStream sock))]
         (.write wrtr resp-string))
    (catch Exception e
      (println e))))
