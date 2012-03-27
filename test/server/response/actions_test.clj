(ns server.response.actions-test
  (:use server.response.actions clojure.test server.core)
  (:import java.io.ByteArrayInputStream
           java.io.ByteArrayOutputStream))

(deftest test-reader-source-1
  (is (= (reader-source "test")
         (str (config "webroot") "test"))))

(deftest test-reader-source-2
  (is (= (reader-source "test test")
         (str (config "webroot") "test test"))))

(deftest test-reader-source-3
  (let [stream (ByteArrayInputStream. (.getBytes ""))]
    (is (= (reader-source stream)
           stream))))

(deftest test-writer-source-1
  (is (= (writer-source "test")
         "/tmp/test")))

(deftest test-writer-source-2
  (is (= (writer-source "test test")
         "/tmp/test test")))

(deftest test-writer-source-3
  (let [stream (ByteArrayOutputStream.)]
    (is (= (writer-source stream) stream))))

(deftest test-echo-1
  (is (= [200 "test"] (echo "test"))))

(deftest test-echo-2
  (is (= [500 "Internal error"] (echo nil))))

(deftest test-serve-file-1
  (let [stream (ByteArrayInputStream. (.getBytes "hello"))]
    (is (= [200 "hello"]
           (serve-file stream)))))

(deftest test-serve-file-2
  (let [stream (ByteArrayInputStream. (.getBytes ""))]
    (is (= [200 ""]
           (serve-file stream)))))

(deftest test-write-file-1
  (let [stream (ByteArrayOutputStream.)]
    (write-file stream "test")
    (is (= "test"
           (.toString stream "UTF-8")))))

(deftest test-write-file-2
  (let [stream (ByteArrayOutputStream.)]
    (write-file stream "")
    (is (= ""
           (.toString stream "UTF-8")))))

(deftest test-write-file-3
  (let [stream (ByteArrayOutputStream.)]
    (write-file stream nil)
    (is (= ""
           (.toString stream "UTF-8")))))
