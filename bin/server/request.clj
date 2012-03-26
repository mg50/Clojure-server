(ns server.request)

(def http-methods [:OPTIONS :GET :HEAD :POST :PUT :DELETE :TRACE :CONNECT])


(defn parse-request-line [request-line]
  (let [methods-regex (->> http-methods
                        (map name)
                        (interpose "|")
                        (apply str))
        regex (re-pattern (str "^(" methods-regex ") ([^\\s]+) ([^\\s]+)$"))
        matches (re-find regex request-line)
        [method request-uri http-version] (rest matches)]
      {:method (keyword method), :request-uri request-uri, :http-version http-version}))


(defn parse-headers [headers]
  (let [header-re #"^([\a-])+ .*$"
        header-pairs* (map (fn [header]
                             (let [h-matches (re-find header-re header)]
                               (if (empty? h-matches)
                                 nil
                                 (rest h-matches))))
                           headers)
        header-pairs (apply concat (remove nil? header-pairs*))]
    (apply hash-map header-pairs)))

(defn parse-request [request]
  (let [request-line (parse-request-line (first request))
        headers (parse-headers (rest request))]
    {:request-line request-line, :headers headers}))
