This is a simple Clojure webserver library utilizing agents and Sinatra-style routing. Include it in your lein dependencies for another project via com.scriptling/clojureserver "0.1.2-SNAPSHOT". The project can't be run on its own.

Any project using the server should have a Java properties file called conf.properties at the top-level directory and be run from there. The file should specify a numagents number, a webroot string and a port number.

To run the unit tests with Leiningen, make sure to `lein compile` first.
