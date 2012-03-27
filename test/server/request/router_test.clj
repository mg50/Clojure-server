(ns server.request.router-test
  (:use server.request.router clojure.test))

(deftest test-route-1
  (let [action (fn [req] req)
        route (GET "[A-Z]" action)]
    (is (= action (:action route)))
    (is (= :GET (:method route)))
    (is (= "^[A-Z]$" (str (:pattern route))))))

(deftest test-route-2
  (let [action (fn [req] 3)
        route (POST "(.+)a" action)]
    (is (= action (:action route)))
    (is (= :POST (:method route)))
    (is (= "^(.+)a$" (str (:pattern route))))))

(deftest test-match-uri-against-pattern-1
  (let [pattern #"/([^/]+)"
        uri "/hello"]
    (is (= {:$1 "hello"}
           (match-uri pattern uri)))))

(deftest test-match-uri-against-pattern-2
  (let [pattern #"/([^/]+)/test/([^/]+)"
        uri "/a/test/b"]
    (is (= {:$1 "a", :$2 "b"}
           (match-uri pattern uri)))))

(deftest test-match-uri-against-pattern-3
  (let [pattern #"/([^/]+)/test/([^/]+)?"
        uri "/a/test/"]
    (is (= {:$1 "a", :$2 nil}
           (match-uri pattern uri)))))

(deftest test-match-uri-against-pattern-4
  (let [pattern #"/([^/]+)/test/([^/]+)"
        uri "/a/test/"]
    (is (nil? (match-uri pattern uri)))))

(deftest test-match-uri-against-pattern-4
  (let [pattern #"/a/test/"
        uri "/a/test/"]
    (is (empty? (match-uri pattern uri)))))


(deftest test-match-uri-against-pseudopattern-1
  (let [pseudopattern "/:a"
        uri "/hello"]
    (is (= {:a "hello"}
           (match-uri pseudopattern uri)))))

(deftest test-match-uri-against-pseudopattern-2
  (let [pseudopattern "/:p1/test/:p2"
        uri "/abc/test/xyz"]
    (is (= {:p1 "abc", :p2 "xyz"}
           (match-uri pseudopattern uri)))))

(deftest test-match-uri-against-pseudopattern-3
  (let [pseudopattern "/:a/123"
        uri "/123"]
    (is (nil? (match-uri pseudopattern uri)))))

(deftest test-match-uri-against-pseudopattern-4
  (let [pseudopattern "/nopattern"
        uri "/nopattern"]
    (is (= {} (match-uri pseudopattern uri)))))


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


(deftest test-defrouter-1
  (defrouter test-router-1 [request params]
    (GET "/test" (inc (:x params))))
  (let [request {:request-line {:method :GET, :request-uri "/test"}
                 :body-params {:x 2}}]
    (is (= 3 (test-router-1 request)))))

(deftest test-defrouter-2
  (defrouter test-router-2 [request params]
    (POST "/:id/a" (dec (Integer/parseInt (:id params)))))
  (let [request {:request-line {:method :POST, :request-uri "/5/a"}}]
    (is (= 4 (test-router-2 request)))))

(deftest test-defrouter-3
  (defrouter test-router-3 [request params]
    (GET "/:id/a"
         (dec (Integer/parseInt (:id params))))
    (ANY* #"/(.*)/b"
          (inc (Integer/parseInt (:$1 params)))))
  (let [request {:request-line {:method :POST, :request-uri "/5/b"}}]
    (is (= 6 (test-router-3 request)))))

(deftest test-defruter-4
  (defrouter test-router-4 [request params]
    (GET "/xyz"
         true))
  (let [request {:request-line {:method :GET, :request-uri "/5/b"}}]
    (is (nil? (test-router-4 request)))))
