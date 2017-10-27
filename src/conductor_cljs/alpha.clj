(ns conductor-cljs.alpha
  (:require [clojure.edn :as edn]))

(def test-ns
  (or (System/getProperty "conductor.cljs-test-ns")
      "orchestra-cljs.spec.test"))

(def printer-fn-impl
  (let [printer (some-> (System/getProperty "conductor.printer-fn")
                        (edn/read-string))]
    (or printer 'expound.alpha/printer)))

(defmacro printer-fn []
  printer-fn-impl)

(defmacro instrument []
  `(~(symbol test-ns "instrument")))

(defmacro unstrument []
  `(~(symbol test-ns "unstrument")))

(defmacro fixture [sym-or-syms]
  `(let [original-explain# (atom nil)
         original-assert# (cljs.spec.alpha/check-asserts?)
         original-formatter# (:formatter (cljs.test/get-current-env))]
     {:before
      #(let [formatter# conductor-cljs.alpha/test-formatter]
         (reset! original-explain# cljs.spec.alpha/*explain-out*)
         (cljs.spec.alpha/check-asserts true)
         (set! cljs.spec.alpha/*explain-out* ~printer-fn-impl)
         (cljs.test/update-current-env! [:formatter] (constantly formatter#))
         (~(symbol test-ns "instrument") ~sym-or-syms))

      :after
      #(do
         (~(symbol test-ns "unstrument") ~sym-or-syms)
         (cljs.spec.alpha/check-asserts original-assert#)
         (set! cljs.spec.alpha/*explain-out* @original-explain#)
         (cljs.test/update-current-env! [:formatter] (constantly original-formatter#)))}))