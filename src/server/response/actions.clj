(ns server.response.actions
  (:use [clojure.java.io :only [writer reader]]
        server.core))

(defmulti reader-source class)
(defmethod reader-source java.lang.String [string]
  (str (config "webroot") string))
(defmethod reader-source java.io.InputStream [stream]
  stream)

(defmulti writer-source class)
(defmethod writer-source java.lang.String [string]
  (str "/tmp/" string))
(defmethod writer-source java.io.OutputStream [stream]
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
