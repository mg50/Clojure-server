(ns server.response.actions
  (:use [clojure.java.io :only [writer reader]]
        server.core))


(defn echo [message]
  (if (nil? message)
    [500 "Internal error"]
    [200 message]))

(defn serve-file [stream-or-path]
  (try
    (with-open [rdr (if (string? stream-or-path)
                      (reader (str
                               (config-property "webroot")
                               stream-or-path))
                      (reader stream-or-path))]
          [200 (slurp rdr)])
    (catch Exception _
      [404 "File not found"])))


(defn write-file [stream-or-filename data]
  (try
    (with-open [wrtr (if (string? stream-or-filename)
                       (writer (str "/tmp/" stream-or-filename))
                       (writer stream-or-filename))]
      (spit wrtr data)
      [200 "File successfully written."])
    (catch Exception _
      [500 "Unable to write file."])))

(comment
  (defn write-file [filename data]
    (try
      (with-open [wrtr (writer (str "/tmp/" filename))]
        (println "hello")
        (.write writer data)
        (println "bye")
        [200 "File successfully written."])
      (catch Exception _
        [500 "Unable to write file."]))))
