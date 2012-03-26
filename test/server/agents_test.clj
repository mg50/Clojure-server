(ns server.agents-test
  (:use clojure.test
        (server agents core
                [response :only [make-response-string]])))

(deftest test-request-response-cycle-1
  (let [request (normalize
                 "GET /test HTTP/1.1"
                 "")
        socket (MockSocket. )]))


(comment
  (deftest test-request-response-cycle-1
    (binding [dispatch (fn [_] [200 "Successful!"])]
      (let [sock (MockSocket. "test")]
        (request-response-cycle sock)
        (is (= (-> sock .getOutputStream .toString)
               (make-response-string 200 "Successul!")))))))
