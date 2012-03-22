(ns server.response-test
  (:use [server.response])
  (:use [clojure.test])
  (:use [server.core]))


(let [resp1 (str "HTTP/1.1 200 OK" crlf
                 "Content-Type: text/plain; charset=utf-8" crlf
                 "Content-Length: 5" crlf
                 "Connection: close" crlf
                 crlf
                 "hello")
      resp2 (str "HTTP/1.1 404 Not Found" crlf
                 "Content-Type: text/plain; charset=utf-8" crlf
                 "Content-Length: 5" crlf
                 "Connection: close" crlf
                 crlf
                 "bad request")]


  (deftest test-make-response-string--1
    (is (= (make-response-string 200 "hello")
           (str "HTTP/1.1 200 OK" crlf
                "Content-Type: text/plain; charset=utf-8" crlf
                "Content-Length: 5" crlf
                "Connection: close" crlf
                crlf
                "hello"))))

  (deftest test-make-response-string-2
    (is (= (make-response-string 404 "bad request")
           (str "HTTP/1.1 404 Not Found" crlf
                "Content-Type: text/plain; charset=utf-8" crlf
                "Content-Length: 11" crlf
                "Connection: close" crlf
                crlf
                "bad request"))))


  (deftest test-response-status-string-1
    (is (= "HTTP/1.1 200 OK"
           (response-status-string resp1))))

  (deftest test-response-status-string-2
    (is (= "HTTP/1.1 404 Not Found"
           (response-status-string resp2))))

  (deftest test-response-body-string-1
    (is (= "hello"
           (response-body-string resp1))))

  (deftest test-response-body-string-2
    (is (= "bad request")
        (response-body-string resp2))))
