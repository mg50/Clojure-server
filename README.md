This is a simple Clojure webserver utilizing agents and Sinatra-style routing. Server configuration can be set in the conf.properties file in the project root; currently, the only properties that are used are "webroot" and "maxthreads."

After downloading the project, make sure to run `lein compile` to compile the src/java directory.

To run the server from the project root, you have three options:

Echo Server:
Default setting: start with `lein run`. Once started, visit "localhost:3000/[URI]" to have the server print the URI back to you in plaintext.

File Server:
Start with `lein run serve-file`. Going to "localhost:3000/[file-path]" will attempt to retrieve the file at the specified path (relative to the webroot configuration) and send it back in plaintext. If not found, the server should send a 404.

File Writer:
Start with `lein run file-write`. Send a POST request to "localhost:3000/store_data" along with `filename` and `data` parameters. The server will attempt to write a new file to the /tmp directory with the specified filename and contents.
