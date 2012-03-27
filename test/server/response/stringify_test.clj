(ns server.response.stringify-test
  (:use server.response.stringify
        clojure.test
        server.core))


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
                "bad request")))))
