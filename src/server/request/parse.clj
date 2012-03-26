(ns server.request.parse
  (:use server.core [clojure.java.io :only [reader]]
        [server.socket-util :only [stream-to-string]])
  (:require [clojure.string :as string])
  (:import (java.io InputStreamReader BufferedReader)))

(def http-methods [:OPTIONS :GET :HEAD :POST :PUT :DELETE :TRACE :CONNECT])


(defn parse-request-line [request-line]
  (let [[method request-uri http-version] (string/split request-line #"\\s+")
        methods-regex (->> http-methods
                        (map name)
                        (interpose "|")
                        (apply str))
        regex (re-pattern (str "^(" methods-regex ") ([^\\s]+) ([^\\s]+)$"))
        matches (re-find regex request-line)
        [method request-uri http-version] (rest matches)]
      {:method (keyword method), :request-uri request-uri, :http-version http-version}))


(defn parse-headers [headers]
  (let [header-re #"^([A-Za-z-]+): (.*)$"
        header-pairs* (map (fn [header]
                             (let [h-matches (re-find header-re header)]
                               (if (empty? h-matches)
                                 nil
                                 [(keyword (nth h-matches 1)) (nth h-matches 2)])))
                           headers)
        header-pairs (apply concat (remove nil? header-pairs*))]
    (apply hash-map header-pairs)))


(defn parse-k-v-pairs [pairs-string]
  (let [undecoded-pairs (if (or (nil? pairs-string) (= "" pairs-string))
                          []
                          (map (fn [k-v-p]
                                 (string/split k-v-p #"="))
                               (string/split pairs-string #"&")))]
    (reduce (fn [hash, [key val]]
              (if (and key val)
                (assoc hash (keyword (url-decode key)) (url-decode val))
                hash))
            {}
            undecoded-pairs)))


(defn parse-request [request-stream]
  (let [rdr (reader request-stream)]
    (let [parsed-request-line (parse-request-line (.readLine rdr))
          parsed-headers (parse-headers (take-while
                                         #(not (#{crlf ""} %))
                                         (repeatedly #(.readLine rdr))))
          body-params (let [content-length (:Content-Length parsed-headers)]
                        (if (or (nil? content-length) (= "0" content-length))
                          {}
                          (let [body-string (stream-to-string rdr (Integer/parseInt content-length))]
                            (parse-k-v-pairs body-string))))]
      {:request-line parsed-request-line
       :headers parsed-headers
       :body-params body-params})))
