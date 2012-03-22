(ns server.socket-util
  (:use [server.request :only [parse-request]])
  (:import (java.io BufferedReader BufferedWriter InputStreamReader OutputStreamWriter)))

(defn line-seq-from-socket [sock]
  (-> sock
    .getInputStream
    InputStreamReader.
    BufferedReader.
    line-seq))

(defn send-message-to-socket [sock resp-string]
  (let [writer (-> sock
                 .getOutputStream
                 OutputStreamWriter.
                 BufferedWriter.)]
    (.write writer resp-string)
    (.close writer)))
