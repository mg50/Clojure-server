(ns server.core
  (:use [clojure.string :only [split]])
  (:import java.net.URLDecoder java.util.Properties java.io.FileInputStream))

(def crlf "\r\n")

(defn normalize [string-seq]
  (apply str (interpose crlf string-seq)))

(defn convert-newline-to-crlf
  )

(defn url-decode [string]
  (URLDecoder/decode string "UTF-8"))

(defn config-property [property-name]
  (let [properties (Properties.)]
    (.load properties (FileInputStream. "conf.properties"))
    (.getProperty properties property-name)))
