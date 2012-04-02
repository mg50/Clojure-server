(ns server.response.stringify-test
  (:use server.response.stringify
        clojure.test
        server.core))


(deftest test-format-headers-1
  (is (= (format-headers {} 0)
         (str
          "Content-Length: 0" crlf
          "Content-Type: text/plain; charset=utf-8" crlf
          "Connection: close" crlf))))

(deftest test-format-headers-2
  (is (= (format-headers {:a 1} 0)
         (str
          "Content-Length: 0" crlf
          "a: 1" crlf
          "Content-Type: text/plain; charset=utf-8" crlf
          "Connection: close" crlf))))

(deftest test-format-headers-3
  (is (= (format-headers {:a 1 :b 2} 0)
         (str
          "Content-Length: 0" crlf
          "a: 1" crlf
          "b: 2" crlf
          "Content-Type: text/plain; charset=utf-8" crlf
          "Connection: close" crlf))))

(deftest test-format-headers-4
  (is (= (format-headers {:a 1 :b 2} 14)
         (str
          "Content-Length: 14" crlf
          "a: 1" crlf
          "b: 2" crlf
          "Content-Type: text/plain; charset=utf-8" crlf
          "Connection: close" crlf))))


(let [resp1 (str "HTTP/1.1 200 OK" crlf
                 "Content-Length: 5" crlf
                 "Content-Type: text/plain; charset=utf-8" crlf
                 "Connection: close" crlf
                 crlf
                 "hello")

      resp2 (str "HTTP/1.1 404 Not Found" crlf
                 "Content-Length: 11" crlf
                 "Content-Type: text/plain; charset=utf-8" crlf
                 "Connection: close" crlf
                 crlf
                 "bad request")

      resp3  (str "HTTP/1.1 200 OK" crlf
                  "Content-Length: 2" crlf
                  "Content-Type: text/plain; charset=utf-8" crlf
                  "Connection: close" crlf
                  crlf
                  "OK")

      resp4  (str "HTTP/1.1 301 Moved Permanently" crlf
                  "Content-Length: 17" crlf
                  "Location: http://www.google.com" crlf
                  "Content-Type: text/plain; charset=utf-8" crlf
                  "Connection: close" crlf
                  crlf
                  "Moved Permanently")]


  (deftest test-make-response-string--1
    (is (= (make-response-string 200 "hello")
           resp1)))

  (deftest test-make-response-string-2
    (is (= (make-response-string 404 "bad request")
           resp2)))

  (deftest test-make-response-string-3
    (is (= (make-response-string {:status-code 200
                                  :body "OK"})
           resp3)))

  (deftest test-make-response-string-4
    (is (= (make-response-string {:status-code 301
                                  :body "Moved Permanently"
                                  :Location "http://www.google.com"})
           resp4))))
