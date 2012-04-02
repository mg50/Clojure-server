(ns server.response.stringify
  (:use [server.core]))

(def status-hash
  {100 "Continue"
   101 "Switching Protocols"
   200 "OK"

   201 "Created"
   202 "Accepted"
   203 "Non-Authoritative Information"
   204 "No Content"
   205 "Reset Content"
   206 "Partial Content"

   300 "Multiple Choices"
   301 "Moved Permanently"
   302 "Found"
   303 "See Other"
   304 "Not Modified"
   305 "Use Proxy"
   307 "Temporary Redirect"

   400 "Bad Request"
   401 "Unauthorized"
   402 "Payment Required"
   403 "Forbidden"
   404 "Not Found"
   405 "Method Not Allowed"
   406 "Not Acceptable"
   407 "Proxy Authentication Required"
   408 "Request Time-out"
   409 "Conflict"
   410 "Gone"
   411 "Length Rquired"
   412 "Precondition Failed"
   413 "Request Entity Too Large"
   414 "Request-URI Too Large"
   415 "Unsupported Media Type"
   416 "Requested range not satisfiable"
   417 "Expectation Failed"

   500 "Internal Server Error"
   501 "Not Implemented"
   502 "Bad Gateway"
   503 "Service Unavailable"
   504 "Gateway Time-out"
   505 "HTTP Version not supported"})

(def default-headers {:Connection "close" :Content-Type "text/plain; charset=utf-8"})

(defn format-headers [headers body-length]
  (let [headers (merge default-headers headers {:Content-Length body-length})]
    (let [header-strings (map #(str (name (first %))
                                    ": "
                                    (second %)
                                    crlf)
                              headers)]
      (apply str header-strings))))

(defn make-response-string
  ([hash]
     (let [status-code (:status-code hash)
           status-line (str "HTTP/1.1 " status-code " " (status-hash status-code))
           headers (apply merge {}
                          (filter #(not (#{:status-code :body} (first %))) hash))
           body (:body hash)]
       (normalize status-line
                  (format-headers headers (count (.getBytes body)))
                  body)))
  ([status-code body]
     (make-response-string {:status-code status-code :body body})))
