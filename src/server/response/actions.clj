(ns server.response.actions
  (:use [clojure.java.io :only [writer reader]]
        server.core)
  (:require [clojure.string :as string])
  (:import java.io.File))

(defmulti reader-source class)
(defmethod reader-source java.lang.String [string]
  (str (config "webroot") "/" string))
(defmethod reader-source java.io.InputStream [stream]
  stream)

(defmulti writer-source class)
(defmethod writer-source java.lang.String [string]
  (str "/tmp/" string))
(defmethod writer-source java.io.OutputStream [stream]
  stream)

(defmulti get-file class)
(defmethod get-file java.lang.String [path]
  (File. (str (config "webroot") "/" path)))
(defmethod get-file java.io.File [file]
  file)


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

(defn dir-list [dir-path-or-file]
  (try
    (let [dir (get-file dir-path-or-file)]
      (if (and (.exists dir) (.isDirectory dir))
        (echo (->> dir
                   .listFiles
                   (map #(.getName %))
                   (string/join "\n")))
        [404 "Directory not found"]))
    (catch Exception e
      (println e)
      [500 "Internal error"])))
