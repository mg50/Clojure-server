(ns server.request.router
  (:use [server.request.parse :only [http-methods]])
  (:require [clojure.string :as string]))

(def any-method :ANY*)

(defmulti match-uri (fn [pattern uri] (class pattern)))
str
(defmethod match-uri java.util.regex.Pattern [pattern uri]
  (let [matches (re-matches pattern uri)]
    (when matches
      (if (string? matches)
        {}
        (apply merge {} (for [i (range 1 (count matches))]
                          [(keyword (str "$" i)) (matches i)]))))))

(defmethod match-uri java.lang.String [pseudopattern uri]
  (let [capture-regex-string "([A-Za-z-\\d]+)"
        pseudo-capture-regex (re-pattern (str ":" capture-regex-string))
        string-to-replace-pseudo-capture "([A-Za-z-\\\\d]+)"

        capture-groups (re-seq pseudo-capture-regex pseudopattern)
        capture-names (map #(-> % second keyword) capture-groups)
        proper-pattern (re-pattern (string/replace pseudopattern pseudo-capture-regex string-to-replace-pseudo-capture))

        matches (re-matches proper-pattern uri)
        capture-pairs (map #(hash-map %1 %2) capture-names (rest matches))]
    (if matches
      (apply merge {} capture-pairs)
      nil)))


(defn route-matcher
  "Given a vector of routes, returns a function that attempts to find the first whose pattern/pseudopattern matches the URI of a request. If one is found, the route's action is called on the request (along with any URI parameters)."
  [routes]
  (fn [request]
    (let [uri (get-in request [:request-line :request-uri])]
      (loop [remaining-routes routes]
        (if-let [current-route (first remaining-routes)]
          (let [pattern (:pattern current-route)]
            (if (#{any-method (get-in request [:request-line :method])} (:method current-route))
              (if-let [uri-matches (match-uri pattern uri)]
                ((:action current-route)
                 request
                 (merge {}
                        (:body-params request)
                        uri-matches))
                (recur (rest remaining-routes)))
              (recur (rest remaining-routes))))
          nil)))))

(defmacro defroute [method]
  `(defn ~(-> method name symbol) [pattern# action#]
     {:method ~method
      :pattern (let [new-pattern-string# (str "^" pattern# "$")]
                 (if (string? pattern#)
                   new-pattern-string#
                   (re-pattern new-pattern-string#)))
      :action action#}))

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
