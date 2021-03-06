# lein-pdo

A leiningen plugin for running multiple tasks in parallel. This is primarily useful
for running multiple tasks where one or more would normally block forever, preventing
future tasks from executing.

If you like this, yell at @technomancy and make him put it in leiningen proper.

This task does not work with < lein 2.

## Usage

Put `[lein-pdo "0.1.1"]` into the `:plugins` vector of your
`:user` profile.

You can use it just like the `do` task:

```
lein pdo cljsbuild auto, repl
```

If you had used the `do` task, `cljsbuild` would block forever and the `repl` task would
never execute. With `pdo`, `cljsbuild auto` runs inside of a future and the repl task
(or any task in the last position) runs in the main thread, doing what you want.

## License

Copyright © 2012 Anthony Grimes

Distributed under the Eclipse Public License, the same as Clojure.
