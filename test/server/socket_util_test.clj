(ns server.socket-util-test
  (:use server.core server.socket-util clojure.test)
  (:import server.java.MockSocket))

(deftest test-line-seq-from-socket-1
  (let [sock (MockSocket. (str "Line 1" crlf "Line 2" crlf "Line 3"))]
    (is (= (line-seq-from-socket sock)
           ["Line 1" "Line 2" "Line 3"]))))

(deftest test-line-seq-from-socket-2
  (let [sock (MockSocket. (str "Line" crlf "Line" crlf))]
    (is (= line-seq-from-socket sock)
        ["Line" "Line"])))

(deftest test-line-seq-from-socket-2
  (let [sock (MockSocket. (str ""))]
    (is (= (line-seq-from-socket sock)
           nil))))

(deftest send-message-to-socket-1
  (let [sock (MockSocket. "")]
    (send-message-to-socket sock "hello world")
    (is (= "hello world"
           (-> sock
               .getOutputStream
               .toString)))))

(deftest send-message-to-socket-2
  (let [sock (MockSocket. "hello world")]
    (send-message-to-socket sock "goodbye world")
    (is (= "goodbye world")
        (-> sock
            .getOutputStream
            .toString))))
