(defproject com.scriptling/clojureserver "0.1.7-SNAPSHOT"
  :description "Simple server written in Clojure"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :plugins [[lein-swank "1.4.4"]]
  :javac-options {:destdir "classes/"}
  :java-source-path "src/server/java")
