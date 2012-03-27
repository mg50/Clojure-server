(ns server.agents-test
  (:use server.agents clojure.test server.core
        [server.response.stringify :only [make-response-string]])
  (:import server.java.MockSocket))

(deftest test-generate-agents
  (with-redefs [config (fn [_] "5")]
    (let [router "test"
          agents (generate-agents router)]
      (is (= 5 (count agents)))
      (doseq [ag agents]
        (is (= "test" @ag))
        (is (= :continue (error-mode ag)))))))

(deftest test-request-response-cycle-1
  (let [request (normalize "GET / HTTP/1.1" "")
        mock-router (fn [request] [200 "OK"])
        socket (MockSocket. request)]
    (request-response-cycle mock-router socket)
    (is (= (make-response-string 200 "OK")
           (-> socket .getOutputStream .toString)))))

(deftest test-request-response-cycle-2
  (let [request (normalize "POST / HTTP/1.1" "")
        mock-router (fn [request] [500 "Internal error"])
        socket (MockSocket. request)]
    (request-response-cycle mock-router socket)
    (is (= (make-response-string 500 "Internal error")
           (-> socket .getOutputStream .toString)))))

(deftest test-send-socket-to-http-agents-1
  (with-redefs [rand-nth first]
    (let [router (fn [_] [404 "Not Found"])
          agents (generate-agents router)
          request (normalize "GET / HTTP/1.1" "")
          socket (MockSocket. request)]
      (send-socket-to-http-agents agents socket)
      (apply await agents)
      (is (= (make-response-string 404 "Not Found")
             (-> socket .getOutputStream .toString))))))

(deftest test-send-socket-to-http-agents-2
  (with-redefs [rand-nth first]
    (let [router (fn [_] [404 "Not Found"])
          agents (generate-agents router)
          request (normalize "GET / HTTP/1.1" "")
          sockets (for [_ (range 1000)]
                    (MockSocket. request))]
      (doseq [sock sockets]
        (send-socket-to-http-agents agents sock))
      (apply await agents)
      (let [resp (make-response-string 404 "Not Found")]
        (doseq [sock sockets]
          (is (= resp (-> sock .getOutputStream .toString))))))))
