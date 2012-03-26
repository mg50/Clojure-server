(ns server.request.router-test
  (:use server.request.router clojure.test))

(deftest test-route-1
  (let [action (fn [req] req)
        route (GET "[A-Z]" action)]
    (is (= action (:action route)))
    (is (= :GET (:method route)))
    (is (= "^[A-Z]$" (str (:pseudopattern route))))))

(deftest test-route-2
  (let [action (fn [req] 3)
        route (POST "(.+)a" action)]
    (is (= action (:action route)))
    (is (= :POST (:method route)))
    (is (= "^(.+)a$" (str (:pseudopattern route))))))

(deftest test-match-uri-against-pseudopattern-1
  (let [pseudopattern "/:a"
        uri "/hello"]
    (is (= {:a "hello"}
           (match-uri-against-pseudopattern pseudopattern uri)))))

(deftest test-match-uri-against-pseudopattern-2
  (let [pseudopattern "/:p1/test/:p2"
        uri "/abc/test/xyz"]
    (is (= {:p1 "abc", :p2 "xyz"}
           (match-uri-against-pseudopattern pseudopattern uri)))))

(deftest test-match-uri-against-pseudopattern-3
  (let [pseudopattern "/:a/123"
        uri "/123"]
    (is (nil? (match-uri-against-pseudopattern pseudopattern uri)))))

(deftest test-match-uri-against-pseudopattern-4
  (let [pseudopattern "/nopattern"
        uri "/nopattern"]
    (is (= {} (match-uri-against-pseudopattern pseudopattern uri)))))


(deftest test-route-matcher-1
  (let [routes [(GET "[A-Za-z]+" (fn [_ _] "first route"))
                (POST "\\d+" (fn [_ _] "second route"))]
        matcher (route-matcher routes)
        req1 {:request-line {:method :GET :request-uri "AAAA"}}
        req2 {:request-line {:method :POST :request-uri "12345"}}
        req3 {:request-line {:method :POST :request-uri "BBBB"}}]
    (is (= (matcher req1) "first route"))
    (is (= (matcher req2) "second route"))
    (is (nil? (matcher req3)))))

(deftest test-route-matcher-2
  (let [routes [(ANY* "/a" (fn [_ _] true))]
         matcher (route-matcher routes)
         req1 {:request-line {:method :GET, :request-uri "/a"}}
         req2 {:request-line {:method :PUT, :request-uri "/a"}}
         req3 {:request-line {:method :GET, :request-uri "/b"}}]
    (is (matcher req1))
    (is (matcher req2))
    (is (not (matcher req3)))))
