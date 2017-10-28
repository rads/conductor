(ns conductor-cljs.alpha
  (:require [clojure.edn :as edn]))

(def test-ns
  (or (System/getProperty "conductor.cljs-test-ns")
      "orchestra-cljs.spec.test"))

(def printer-fn
  (let [printer (some-> (System/getProperty "conductor.printer-fn")
                        (edn/read-string))]
    (or printer 'expound.alpha/printer)))

(defmacro fixture [sym-or-syms]
  `(let [original-explain# (atom nil)
         original-assert# (cljs.spec.alpha/check-asserts?)
         original-formatter# (:formatter (cljs.test/get-current-env))]
     {:before
      #(let [formatter# conductor-cljs.alpha/test-formatter]
         (reset! original-explain# cljs.spec.alpha/*explain-out*)
         (cljs.spec.alpha/check-asserts true)
         (set! cljs.spec.alpha/*explain-out* ~printer-fn)
         (cljs.test/update-current-env! [:formatter] (constantly formatter#))
         (~(symbol test-ns "instrument") ~sym-or-syms))

      :after
      #(do
         (~(symbol test-ns "unstrument") ~sym-or-syms)
         (cljs.spec.alpha/check-asserts original-assert#)
         (set! cljs.spec.alpha/*explain-out* @original-explain#)
         (cljs.test/update-current-env! [:formatter] (constantly original-formatter#)))}))

(defmacro instrument []
  `(let [instrumented# (~(symbol test-ns "instrument"))]
     (when-not @enabled
       (reset! original-formatter (:formatter (cljs.test/get-current-env)))
       (reset! original-assert (cljs.spec.alpha/check-asserts?))
       (reset! original-explain cljs.spec.alpha/*explain-out*)
       (cljs.spec.alpha/check-asserts true)
       (set! cljs.spec.alpha/*explain-out* ~printer-fn)
       (cljs.test/update-current-env! [:formatter] (constantly test-formatter))
       (reset! enabled true))
     instrumented#))

(defmacro unstrument []
  `(when @enable
     (cljs.spec.alpha/check-asserts @original-assert)
     (set! cljs.spec.alpha/*explain-out* @original-explain)
     (cljs.test/update-current-env! [:formatter] (constantly @original-formatter))
     (~(symbol test-ns "instrument"))
     (reset! enabled false)
     nil))
