# Dockerfile for Akvo Reflow Clojure app

# https://hub.docker.com/_/clojure/
FROM clojure

# Build
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app
RUN lein uberjar

# Run
CMD ["java", "-jar", "target/akvo-reflow.jar"]
