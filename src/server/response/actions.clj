(ns server.response.actions
  (:use [clojure.java.io :only [writer reader]]
        server.core))

(defmulti reader-source string?)
(defmethod reader-source true [string]
  (str (config "webroot") string))
(defmethod reader-source false [stream]
  stream)

(defmulti writer-source string?)
(defmethod writer-source true [string]
  (str "/tmp/" string))
(defmethod writer-source false [stream]
  stream)

(defn echo [message]
  (if message
    [200 message]
    [500 "Internal error"]))

(defn serve-file [stream-or-path]
  (try
    (with-open [rdr (reader (reader-source stream-or-path))]
          [200 (slurp rdr)])
    (catch Exception _
      [404 "File not found"])))


(defn write-file [stream-or-filename data]
  (try
    (with-open [wrtr (writer (writer-source stream-or-filename))]
      (spit wrtr data)
      [200 "File successfully written."])
    (catch Exception _
      [500 "Unable to write file."])))
