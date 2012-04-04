(ns server.core
  (:use [clojure.string :only [split]]
        [clojure.java.io :only [resource]])
  (:import java.net.URLDecoder java.util.Properties java.io.FileInputStream))

(def crlf "\r\n")

(defn normalize [& lines]
  (let [string (apply str (interpose crlf lines))]
    (if (= (last lines) "")
      (str string crlf)
      string)))

(defn url-decode [string]
  (URLDecoder/decode string "UTF-8"))


(defn get-config
  ([stream]
     (let [properties (Properties.)]
       (.load properties stream)
       (into {} properties)))
  ([stream opts]
     (merge (get-config stream) opts)))


(def ^:dynamic *config*
  (get-config
   (-> (Thread/currentThread)
       .getContextClassLoader
       (.getResourceAsStream "conf.properties"))))


(defn initialize-config [opts]
  (def ^:dynamic *config*
    (get-config
     (-> (Thread/currentThread)
         .getContextClassLoader
         (.getResourceAsStream "conf.properties"))
     opts)))

(defn config [key]
  (*config* key))
