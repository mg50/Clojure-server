(ns server.app-test
  (:use clojure.test server.app))

(deftest test-parse-options-1
  (let [opts ["-p" "3000" "-d" "./"]]
    (is (= (parse-options opts)
           {"port" "3000" "webroot" "./"}))))

(deftest test-parse-options-2
  (let [opts ["-p" "3000"]]
    (is (= (parse-options opts)
           {"port" "3000"}))))

(deftest test-parse-options-3
  (let [opts []]
    (is (= (parse-options opts)
           {}))))

(deftest test-parse-options-4
  (let [opts ["-d" "/public" "invalid" 1]]
    (is (= (parse-options opts)
           {"webroot" "/public"}))))
