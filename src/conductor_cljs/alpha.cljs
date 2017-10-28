(ns conductor-cljs.alpha
  (:require
   [cljs.spec.alpha :as s]
   [cljs.spec.test.alpha :as stest]
   [cljs.test :as test])
  (:require-macros [conductor-cljs.alpha :refer [instrument unstrument printer-fn]]))

(def ^:private enabled (atom false))

(def ^:private original-formatter (atom nil))
(def ^:private original-assert (atom nil))
(def ^:private original-explain (atom nil))

(defn test-formatter [v]
  (if (instance? js/Error v)
    (.-message v)
    v))

(defn- start-instrument []
  (let [instrumented (instrument)]
    (when-not @enabled
      (reset! original-formatter (:formatter (test/get-current-env)))
      (reset! original-assert (s/check-asserts?))
      (reset! original-explain s/*explain-out*)
      (s/check-asserts true)
      (set! s/*explain-out* (printer-fn))
      (test/update-current-env! [:formatter] (constantly test-formatter))
      (reset! enabled true))
    instrumented))

(defn- stop-instrument []
  (when @enabled
    (s/check-asserts @original-assert)
    (set! s/*explain-out* @original-explain)
    (test/update-current-env! [:formatter] (constantly @original-formatter))
    (unstrument)
    (reset! enabled false)
    nil))

(defn auto-instrument [flag]
  (if flag
    (start-instrument)
    (stop-instrument)))
