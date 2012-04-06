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

(def config-defaults
  {"webroot" "./public"
   "numagents" "10"
   "port" "5000"})

(defn get-config
  ([stream]
     (let [properties (Properties.)]
       (.load properties stream)
       (into {} properties)))
  ([stream opts]
     (merge (get-config stream) opts)))


(def *config* (atom config-defaults))

(defn initialize-config [opts]
  (reset! *config*
    (try
      (with-open [stream (-> (Thread/currentThread)
                             .getContextClassLoader
                             (.getResourceAsStream "conf.properties"))]
        (get-config stream opts))
      (catch Exception e
        (println "Unable to open conf.properties file. Using config defaults.")
        (merge config-defaults opts)))))

(defn config [key]
  (@*config* key))
