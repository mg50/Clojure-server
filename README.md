This is a simple Clojure webserver utilizing agents and Sinatra-style routing. Server configuration can be set in the conf.properties file in the project root; currently, the only properties that are used are "webroot" and "maxthreads."

After downloading the project, make sure to run `lein compile` to compile the src/java directory.

Type `lein run` to start the server. It responds in the following ways:

If you send a GET request to /static/[file-path], it attempts to retrieve and echo the contents of the file at the file path (relative to the configuration webroot). If no such file is found, the server returns a 404 error.

If you send a POST request to /store_data along with `filename` and `data` parameters, it creates a file in the /tmp folder with corresponding filename and contents.

If you send a GET request to a URI not beginning with "/static/", the server will echo back the URI.

Any other request received a 403 Forbidden response.
