(ns server.request.router
  (:use [server.request.parse :only [http-methods]])
  (:require [clojure.string :as string]))

(def any-method :ANY*)


(defn match-uri-against-pseudopattern [pseudopattern uri]
  (let [capture-regex-string* "([A-Za-z-\\d]+)"
        pseudo-capture-regex (re-pattern (str ":" capture-regex-string*))
        capture-regex-string "([A-Za-z-\\\\d]+)"

        capture-names* (re-seq pseudo-capture-regex pseudopattern)
        capture-names (map #(-> % second keyword) capture-names*)
        proper-pattern (re-pattern (string/replace pseudopattern pseudo-capture-regex capture-regex-string))

        matches (re-matches proper-pattern uri)
        capture-pairs (map #(hash-map %1 %2) capture-names (rest matches))]
    (if matches
      (apply merge {} capture-pairs)
      nil)))


(defn route-matcher [routes]
  (fn [request]
    (loop [remaining-routes routes]
      (if-let [current-route (first remaining-routes)]
        (if (#{:ANY* (get-in request [:request-line :method])} (:method current-route))
          (if-let [uri-matches (match-uri-against-pseudopattern (:pseudopattern current-route) (get-in request [:request-line :request-uri]))]
            ((:action current-route)
             request
             (merge {}
                    (:body-params request)
                    uri-matches))
            (recur (rest remaining-routes)))
          (recur (rest remaining-routes)))
        nil))))

(defmacro defroute [method]
  `(defn ~(-> method name symbol) [pseudopattern# action#]
     {:method ~method, :pseudopattern (str "^" pseudopattern# "$"), :action action#}))

(defmacro defroute-for-http-methods []
  `(do
     ~@(map #(list 'defroute %) (conj http-methods any-method))))

(defroute-for-http-methods)

(defmacro defrouter [name-sym [request-sym params-sym] & forms]
  (let [routes (map (fn [form]
                      `(~(first form)
                        ~(second form)
                        (fn [~request-sym ~params-sym]
                          ~@(rest (rest form)))))
                    forms)]
    `(def ~name-sym (route-matcher [~@routes]))))
