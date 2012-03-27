(ns server.core
  (:use [clojure.string :only [split]])
  (:import java.net.URLDecoder java.util.Properties java.io.FileInputStream))

(def crlf "\r\n")

(defn normalize [& lines]
  (let [string (apply str (interpose crlf lines))]
    (if (= (last lines) "")
      (str string crlf)
      string)))

(defn url-decode [string]
  (URLDecoder/decode string "UTF-8"))

(defn get-config [stream]
  (let [properties (Properties.)]
    (.load properties stream)
    (into {} properties)))

(def config (get-config (FileInputStream. "conf.properties")))
