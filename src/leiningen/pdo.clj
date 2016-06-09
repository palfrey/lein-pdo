(ns leiningen.pdo
  (:require [leiningen.do :refer [group-args]]
            [leiningen.core.main :refer [apply-task lookup-alias]]))

(defn apply-in-future [project task-name args]
  (future (apply-task (lookup-alias task-name project) project args)))

(defn ^:no-project-needed ^:higher-order pdo
  "Higher-order task to perform other tasks in parallel.

Each comma-separated group should be a task named followed by optional arguments.
Each task will be executed in a separate future. The last task will be executed
in the current (main) thread. After it finishes, each future will be dereffed in
order to prevent Leiningen from exiting before all tasks have finished.

If :pdo { :stop-on-last-task-exit true} is set in project.clj, then when the last
task exits we don't wait for the others, but default behaviour is to wait for all tasks.

This task is primarily useful for running multiple tasks that block forever.

USAGE: lein pdo cljsbuild auto, repl"
  [project & args]
  (let [[last parallel] ((juxt last butlast) (group-args args))
        futures (when (seq parallel)
                  (doall
                   (for [[task-name & args] parallel]
                     (apply-in-future project task-name args))))
        [task-name & args] last
        stop-on-last-task-exit (or (-> project :pdo :stop-on-last-task-exit) false)]
    (apply-task (lookup-alias task-name project) project args)
    (if (not stop-on-last-task-exit)
        (doseq [task futures]
          @task))))
