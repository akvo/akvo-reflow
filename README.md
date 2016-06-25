# akvo-reflow [![Build Status](https://travis-ci.org/akvo/akvo-reflow.svg?branch=master)](https://travis-ci.org/akvo/akvo-reflow)

FIXME: description

## Developing

### Setup

When you first clone this repository, run:

```sh
lein setup
```

This will create files for local configuration, and prep your system
for the project.

### Environment

To begin developing, start with a REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

First time init the DB
```clojure
user=> (migrate)
[0]
```


Run `go` to initiate and start the system.

```clojure
dev=> (go)
:started
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

### Testing

Testing uses [lein-test-refresh](https://github.com/jakemcc/lein-test-refresh).
Open a terminal in the project root and enter

```sh
lein test-refresh
```

This will run the tests and automatically re-run the tests as soon as a file is changed.

Of course you can also run tests directly in Leiningen.

```sh
lein test
```

### Generators

This project has several generator functions to help you create files.

To create a new endpoint:

```clojure
dev=> (gen/endpoint "bar")
Creating file src/foo/endpoint/bar.clj
Creating file test/foo/endpoint/bar_test.clj
Creating directory resources/foo/endpoint/bar
nil
```

To create a new component:

```clojure
dev=> (gen/component "baz")
Creating file src/foo/component/baz.clj
Creating file test/foo/component/baz_test.clj
nil
```

## Deploying

FIXME: steps to deploy

## Legal

Copyright © 2016 FIXME
