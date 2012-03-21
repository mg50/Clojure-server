(ns server.request-test
  (:use [server.request])
  (:use [clojure.test]))

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

(deftest parse-request-test

  (is (= (parse-request ["GET / HTTP/1.1"
                         "Host: localhost:3000"
                         "Connection: keep-alive"
                         "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.63 Safari/535.7"
                         "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                         "Accept-Encoding: gzip,deflate,sdch"
                         "Accept-Language: en-US,en;q=0.8"
                         "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3"])

         {:request-line {:method :GET
                         :request-uri "/"
                         :http-version "HTTP/1.1"}
          :headers {:Host "localhost:3000"
                    :Connection "keep-alive"
                    :User-Agent "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.63 Safari/535.7"
                    :Accept "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                    :Accept-Encoding "gzip,deflate,sdch"
                    :Accept-Language "en-US,en;q=0.8"
                    :Accept-Charset "ISO-8859-1,utf-8;q=0.7,*;q=0.3"}
          })))
