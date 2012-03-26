(ns server.request.parse-test
  (:use [server.request.parse] server.core)
  (:use [clojure.test])
  (:import java.io.ByteArrayInputStream))

(do (deftest test-parse-request-line-1
  (is (= (parse-request-line "GET / HTTP/1.1")
         {:method :GET
          :request-uri "/"
          :http-version "HTTP/1.1"})))

(deftest test-parse-request-line-2
  (is (= (parse-request-line "POST /foo HTTP/1.1")
         {:method :POST
          :request-uri "/foo"
          :http-version "HTTP/1.1"})))

(deftest test-parse-request-line-3
  (is (= (parse-request-line "PUT /foo/bar?baz=1 HTTP/1.1")
         {:method :PUT
          :request-uri "/foo/bar?baz=1"
          :http-version "HTTP/1.1"}))))

(deftest test-parse-headers-1
  (is (= (parse-headers ["Host: localhost:3000"])
         {:Host "localhost:3000"})))


(deftest test-parse-headers-2
  (is (=  (parse-headers ["Host: localhost:3000" "Connection: keep-alive" "Accept-Encoding: gzip,deflate,sdch"])
          {:Host "localhost:3000", :Connection "keep-alive", :Accept-Encoding "gzip,deflate,sdch"})))

(deftest test-parse-headers-3
  (is (= (parse-headers ["Host: localhost:3000" "Connection: keep-alive" "Accept-Encoding: gzip,deflate,sdch", "invalid header"])
         {:Host "localhost:3000", :Connection "keep-alive", :Accept-Encoding "gzip,deflate,sdch"})))

(deftest parse-k-v-pairs-1
  (let [kv-string "a=1&b=2"]
    (is (= {:a "1", :b "2"}
           (parse-k-v-pairs kv-string)))))

(deftest parse-k-v-pairs-2
  (let [kv-string "a=1&b=2&"]
    (is (= {:a "1", :b "2"}
           (parse-k-v-pairs kv-string)))))

(deftest parse-k-v-pairs-3
  (let [kv-string ""]
    (is (= {}
           (parse-k-v-pairs kv-string)))))

(deftest parse-k-v-pairs-4
  (let [kv-string "a=1&b=2&c=&d=4"]
    (is (= {:a "1", :b "2", :d "4"}
           (parse-k-v-pairs kv-string)))))


(deftest parse-request-test-1
  (is (= (parse-request (ByteArrayInputStream.
                         (.getBytes
                          (str
                            "GET /xyz/3 HTTP/1.1" crlf
                            "Host: localhost:3000" crlf
                            "Connection: keep-alive" crlf
                            crlf))))
         {:request-line {:method :GET
                         :request-uri "/xyz/3"
                         :http-version "HTTP/1.1"}
          :headers {:Host "localhost:3000"
                    :Connection "keep-alive"}
          :body-params {}})))

(deftest parse-request-test-2
  (is (= (parse-request (ByteArrayInputStream.
                         (.getBytes
                          (str
                           "GET /xyz/3 HTTP/1.1" crlf
                           "Host: localhost:3000" crlf
                           "Connection: keep-alive" crlf
                           "Content-Length: 13" crlf
                           crlf
                           "message=hello"))))
         {:request-line {:method :GET
                         :request-uri "/xyz/3"
                         :http-version "HTTP/1.1"}
          :headers {:Host "localhost:3000"
                    :Connection "keep-alive"
                    :Content-Length "13"}
          :body-params {:message "hello"}})))

(deftest parse-request-test-3
  (is (= (parse-request (ByteArrayInputStream.
                         (.getBytes
                          (str
                           "GET /xyz/3 HTTP/1.1" crlf
                           "Host: localhost:3000" crlf
                           "Connection: keep-alive" crlf
                           "Content-Length: 5" crlf
                           crlf
                           "message"))))
         {:request-line {:method :GET
                         :request-uri "/xyz/3"
                         :http-version "HTTP/1.1"}
          :headers {:Host "localhost:3000"
                    :Connection "keep-alive"
                    :Content-Length "5"}
          :body-params {}})))


(deftest parse-request-test-4
  (is (= (parse-request (ByteArrayInputStream.
                         (.getBytes
                          (str
                           "GET /xyz/3 HTTP/1.1" crlf
                           "Host: localhost:3000" crlf
                           "Connection: keep-alive" crlf
                           crlf
                           "message=hello"))))
         {:request-line {:method :GET
                         :request-uri "/xyz/3"
                         :http-version "HTTP/1.1"}
          :headers {:Host "localhost:3000"
                    :Connection "keep-alive"}
          :body-params {}})))
