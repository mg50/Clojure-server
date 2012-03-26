(defproject server "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :plugins [[lein-swank "1.4.4"]]
  :javac-options {:destdir "classes/"}
  :java-source-path "src/server/java"
  :main server.main)
