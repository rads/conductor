(ns conductor.alpha
  (:refer-clojure :exclude [*out*])
  (:require
   [clojure.spec.alpha :as s]
   [clojure.edn :as edn]))

(def test-ns
  (or (System/getProperty "conductor.clj-test-ns")
      "orchestra.spec.test"))

(def printer-fn-impl
  (let [printer (some-> (System/getProperty "conductor.printer-fn")
                        (edn/read-string))]
    (or printer 'expound.alpha/printer)))

(defmacro fixture [sym-or-syms]
  `(fn [f]
     (let [original-assert# (s/check-asserts?)]
       (s/check-asserts true)
       (binding [s/*explain-out* ~printer-fn-impl]
         (~(symbol test-ns "instrument") ~sym-or-syms)
         (f)
         (~(symbol test-ns "unstrument") ~sym-or-syms)
         (s/check-asserts original-assert#)))))

(def ^:private auto-instrument-running (atom false))

(declare start-instrument stop-instrument)

(defmacro instrument-loop []
  `(future
    (loop []
      (when @auto-instrument-running
        (~(symbol test-ns "instrument")))
      (Thread/sleep 1000)
      (recur))))

(defn start-instrument []
  (when-not @auto-instrument-running
    (reset! auto-instrument-running true)
    (instrument-loop)))

(defn stop-instrument []
  (when @auto-instrument-running
    (reset! auto-instrument-running false)))

(defn auto-instrument [flag]
  (if flag
    (start-instrument)
    (stop-instrument))
  nil)
